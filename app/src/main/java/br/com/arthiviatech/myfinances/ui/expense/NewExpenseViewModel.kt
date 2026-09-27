package br.com.arthiviatech.myfinances.ui.expense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.arthiviatech.myfinances.data.repository.CardRepository
import br.com.arthiviatech.myfinances.data.repository.CategoryRepository
import br.com.arthiviatech.myfinances.data.repository.ExpenseRepository
import br.com.arthiviatech.myfinances.data.repository.RecurringExpenseRepository
import br.com.arthiviatech.myfinances.data.room.entitys.ExpenseEntryEntity
import br.com.arthiviatech.myfinances.data.room.entitys.InstallmentPlanEntity
import br.com.arthiviatech.myfinances.data.room.entitys.RecurringExpenseEntity
import br.com.arthiviatech.myfinances.domain.usecase.CreateInstallmentPlanUseCase
import br.com.arthiviatech.myfinances.domain.usecase.CalculateCardDueDateUseCase
import br.com.arthiviatech.myfinances.core.AppClock
import br.com.arthiviatech.myfinances.ui.input.formatDateInput
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private val DateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
private val MonthFormatter = DateTimeFormatter.ofPattern("MM/yyyy")

data class ExpenseOptionUi(
    val id: Long,
    val label: String,
    val dueDay: Int? = null,
    val closingDay: Int? = null,
)

enum class ExpensePaymentMethod(val label: String, val storedValue: String) {
    PIX("Pix", "PIX"),
    CASH("Dinheiro", "CASH"),
    DEBIT("Débito", "DEBIT"),
    CARD("Cartão de crédito", "CARD"),
}

data class NewExpenseUiState(
    val description: String = "",
    val amount: String = "",
    val categoryId: Long? = null,
    val purchaseDate: String = "",
    val dueDate: String = "",
    val paymentMethod: ExpensePaymentMethod = ExpensePaymentMethod.PIX,
    val cardId: Long? = null,
    val cardDueMonth: String = YearMonth.now().format(MonthFormatter),
    val isInstallment: Boolean = false,
    val installmentCount: String = "",
    val installmentAmount: String = "",
    val isRecurring: Boolean = false,
    val categories: List<ExpenseOptionUi> = emptyList(),
    val cards: List<ExpenseOptionUi> = emptyList(),
    val isSaving: Boolean = false,
    val saved: Boolean = false,
    val errorMessage: String? = null,
)

class NewExpenseViewModel(
    categoryRepository: CategoryRepository,
    cardRepository: CardRepository,
    private val expenseRepository: ExpenseRepository,
    private val recurringExpenseRepository: RecurringExpenseRepository,
    private val createInstallmentPlan: CreateInstallmentPlanUseCase,
    private val calculateCardDueDate: CalculateCardDueDateUseCase,
    private val clock: AppClock,
) : ViewModel() {
    private val form = MutableStateFlow(NewExpenseUiState(
        purchaseDate = clock.today().format(DateFormatter),
        dueDate = clock.today().format(DateFormatter),
    ))
    private val enabledCards = cardRepository.observeEnabled()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val uiState = combine(
        form,
        categoryRepository.observeEnabled(),
        enabledCards,
    ) { state, categories, cards ->
        state.copy(
            categories = categories.map { ExpenseOptionUi(it.id, it.name) },
            cards = cards.map { ExpenseOptionUi(it.id, it.name, it.dueDay, it.closingDay) },
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), NewExpenseUiState())

    fun updateDescription(value: String) = update { copy(description = value, errorMessage = null) }
    fun updateAmount(value: String) = update { copy(amount = value, errorMessage = null) }
    fun selectCategory(id: Long) = update { copy(categoryId = id, errorMessage = null) }
    fun updatePurchaseDate(value: String) = update { copy(purchaseDate = formatDateInput(value), errorMessage = null).recalculateCardDueDate() }
    fun updateDueDate(value: String) = update { copy(dueDate = formatDateInput(value), errorMessage = null) }
    fun selectPaymentMethod(value: ExpensePaymentMethod) = update {
        copy(paymentMethod = value, cardId = if (value == ExpensePaymentMethod.CARD) cardId else null).recalculateCardDueDate()
    }
    fun selectCard(id: Long) = update { copy(cardId = id, errorMessage = null).recalculateCardDueDate() }
    fun updateCardDueMonth(value: String) = update {
        copy(cardDueMonth = value.filter { it.isDigit() || it == '/' }.take(7), errorMessage = null).recalculateCardDueDate()
    }
    fun updateInstallmentCount(value: String) = update { copy(installmentCount = value.filter(Char::isDigit)) }
    fun updateInstallmentAmount(value: String) = update { copy(installmentAmount = value) }
    fun setInstallment(value: Boolean) = update {
        copy(isInstallment = value, isRecurring = if (value) false else isRecurring)
    }
    fun setRecurring(value: Boolean) = update {
        copy(isRecurring = value, isInstallment = if (value) false else isInstallment)
    }

    fun save() {
        val state = form.value
        val validation = validate(state)
        if (validation != null) {
            update { copy(errorMessage = validation) }
            return
        }

        viewModelScope.launch {
            update { copy(isSaving = true, errorMessage = null) }
            runCatching { persist(state) }
                .onSuccess { update { copy(isSaving = false, saved = true) } }
                .onFailure { error ->
                    update {
                        copy(
                            isSaving = false,
                            errorMessage = error.message ?: "Não foi possível salvar a despesa.",
                        )
                    }
                }
        }
    }

    fun consumeSaved() = update { copy(saved = false) }

    private suspend fun persist(state: NewExpenseUiState) {
        val categoryId = requireNotNull(state.categoryId)
        val purchaseDate = LocalDate.parse(state.purchaseDate, DateFormatter)
        val dueDate = LocalDate.parse(state.dueDate, DateFormatter)
        val amountCents = parseMoneyToCents(state.amount)
        val cardId = state.cardId.takeIf { state.paymentMethod == ExpensePaymentMethod.CARD }

        when {
            state.isInstallment -> {
                val installmentAmountCents = parseMoneyToCents(state.installmentAmount)
                val installmentCount = state.installmentCount.toInt()
                createInstallmentPlan(
                    plan = InstallmentPlanEntity(
                        description = state.description.trim(),
                        installmentCount = installmentCount,
                        installmentAmountCents = installmentAmountCents,
                        informationalTotalCents = installmentAmountCents * installmentCount,
                        firstDueDateEpochDay = dueDate.toEpochDay(),
                        categoryId = categoryId,
                        paymentMethod = state.paymentMethod.storedValue,
                        cardId = cardId,
                    ),
                    purchaseDate = purchaseDate,
                )
            }
            state.isRecurring -> recurringExpenseRepository.insert(
                RecurringExpenseEntity(
                    description = state.description.trim(),
                    categoryId = categoryId,
                    dueDay = dueDate.dayOfMonth,
                    paymentMethod = state.paymentMethod.storedValue,
                    cardId = cardId,
                    startMonth = YearMonth.from(dueDate).toReferenceMonth(),
                ),
            )
            else -> expenseRepository.insert(
                ExpenseEntryEntity(
                    description = state.description.trim(),
                    amountCents = amountCents,
                    categoryId = categoryId,
                    purchaseDateEpochDay = purchaseDate.toEpochDay(),
                    dueDateEpochDay = dueDate.toEpochDay(),
                    referenceMonth = YearMonth.from(dueDate).toReferenceMonth(),
                    paymentMethod = state.paymentMethod.storedValue,
                    cardId = cardId,
                    origin = "EVENTUAL",
                ),
            )
        }
    }

    private fun validate(state: NewExpenseUiState): String? = when {
        state.description.isBlank() -> "Informe a descrição."
        parseMoneyToCents(state.amount) <= 0 && !state.isRecurring -> "Informe um valor maior que zero."
        state.categoryId == null -> "Selecione uma categoria."
        parseDate(state.purchaseDate) == null -> "Informe uma data de compra válida."
        parseDate(state.dueDate) == null -> "Informe uma data de vencimento válida."
        state.paymentMethod == ExpensePaymentMethod.CARD && state.cardId == null -> "Selecione o cartão."
        state.paymentMethod == ExpensePaymentMethod.CARD && state.selectedCard()?.closingDay == null && parseMonth(state.cardDueMonth) == null ->
            "Informe o mês do primeiro vencimento no formato mm/aaaa."
        state.isInstallment && (state.installmentCount.toIntOrNull() ?: 0) <= 0 -> "Informe a quantidade de parcelas."
        state.isInstallment && parseMoneyToCents(state.installmentAmount) <= 0 -> "Informe o valor da parcela."
        else -> null
    }

    private fun update(transform: NewExpenseUiState.() -> NewExpenseUiState) {
        form.update(transform)
    }

    private fun NewExpenseUiState.selectedCard() = cardId?.let { id -> enabledCards.value.firstOrNull { it.id == id } }

    private fun NewExpenseUiState.recalculateCardDueDate(): NewExpenseUiState {
        if (paymentMethod != ExpensePaymentMethod.CARD) return this
        val card = selectedCard() ?: return this
        val purchase = parseDate(purchaseDate) ?: return this
        val selectedMonth = if (card.closingDay == null) parseMonth(cardDueMonth) else null
        if (card.closingDay == null && selectedMonth == null) return this
        val calculated = calculateCardDueDate(purchase, card.dueDay, card.closingDay, selectedMonth)
        return copy(dueDate = calculated.format(DateFormatter))
    }
}

internal fun parseMoneyToCents(value: String): Long {
    val normalized = value.trim().replace("R$", "").replace(" ", "").replace(".", "").replace(',', '.')
    return runCatching {
        normalized.toBigDecimalOrNull()?.movePointRight(2)?.longValueExact() ?: 0
    }.getOrDefault(0)
}

private fun parseDate(value: String): LocalDate? = runCatching {
    LocalDate.parse(value, DateFormatter)
}.getOrNull()

private fun parseMonth(value: String): YearMonth? = runCatching {
    val parts = value.split('/')
    require(parts.size == 2)
    YearMonth.of(parts[1].toInt(), parts[0].toInt())
}.getOrNull()

private fun YearMonth.toReferenceMonth(): Int = year * 100 + monthValue
