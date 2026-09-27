package br.com.arthiviatech.myfinances.ui.expense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.arthiviatech.myfinances.data.repository.CardRepository
import br.com.arthiviatech.myfinances.data.repository.CategoryRepository
import br.com.arthiviatech.myfinances.data.repository.ExpenseRepository
import br.com.arthiviatech.myfinances.data.room.entitys.ExpenseEntryEntity
import br.com.arthiviatech.myfinances.domain.usecase.DisableInstallmentsFromUseCase
import br.com.arthiviatech.myfinances.core.AppClock
import br.com.arthiviatech.myfinances.ui.input.formatDateInput
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExpenseDetailViewModel(
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository,
    private val cardRepository: CardRepository,
    private val disableInstallmentsFrom: DisableInstallmentsFromUseCase,
    private val clock: AppClock,
) : ViewModel() {
    private val mutableState = MutableStateFlow(ExpenseDetailUiState())
    val uiState = mutableState.asStateFlow()
    private var expenseId: Long? = null
    private var currentEntity: ExpenseEntryEntity? = null

    init {
        viewModelScope.launch {
            categoryRepository.observeEnabled().collect { categories ->
                mutableState.update { state -> state.copy(categories = categories.map { ExpenseOptionUi(it.id, it.name) }) }
            }
        }
        viewModelScope.launch {
            cardRepository.observeEnabled().collect { cards ->
                mutableState.update { state -> state.copy(cards = cards.map { ExpenseOptionUi(it.id, it.name) }) }
            }
        }
    }

    fun load(id: Long) {
        if (expenseId == id && mutableState.value.expense != null) return
        expenseId = id
        viewModelScope.launch { refresh() }
    }

    fun requestPayment() = mutableState.update { it.copy(showPaymentConfirmation = true) }
    fun dismissPayment() = mutableState.update { it.copy(showPaymentConfirmation = false) }
    fun clearMessage() = mutableState.update { it.copy(message = null) }

    fun openEditor() {
        val entity = currentEntity ?: return
        val detail = mutableState.value.expense ?: return
        mutableState.update {
            it.copy(
                editor = ExpenseEditUi(
                    description = entity.description,
                    amount = formatEditableMoney(entity.amountCents),
                    categoryId = entity.categoryId,
                    purchaseDate = LocalDate.ofEpochDay(entity.purchaseDateEpochDay).format(DetailDateFormatter),
                    dueDate = LocalDate.ofEpochDay(entity.dueDateEpochDay).format(DetailDateFormatter),
                    paymentMethod = ExpensePaymentMethod.entries.firstOrNull { method -> method.storedValue == entity.paymentMethod } ?: ExpensePaymentMethod.PIX,
                    cardId = entity.cardId,
                    currentCategoryLabel = detail.category,
                    currentCardLabel = detail.card,
                ),
            )
        }
    }
    fun dismissEditor() = mutableState.update { it.copy(editor = null) }
    fun updateEditDescription(value: String) = updateEditor { copy(description = value, errorMessage = null) }
    fun updateEditAmount(value: String) = updateEditor { copy(amount = value, errorMessage = null) }
    fun selectEditCategory(id: Long) = updateEditor { copy(categoryId = id, currentCategoryLabel = "", errorMessage = null) }
    fun updateEditPurchaseDate(value: String) = updateEditor { copy(purchaseDate = formatDateInput(value), errorMessage = null) }
    fun updateEditDueDate(value: String) = updateEditor { copy(dueDate = formatDateInput(value), errorMessage = null) }
    fun selectEditPaymentMethod(method: ExpensePaymentMethod) = updateEditor {
        copy(paymentMethod = method, cardId = if (method == ExpensePaymentMethod.CARD) cardId else null, errorMessage = null)
    }
    fun selectEditCard(id: Long) = updateEditor { copy(cardId = id, currentCardLabel = null, errorMessage = null) }

    fun saveEditor() {
        val editor = mutableState.value.editor ?: return
        val entity = currentEntity ?: return
        val amount = parseMoneyToCents(editor.amount)
        val purchaseDate = editor.purchaseDate.toDetailDate()
        val dueDate = editor.dueDate.toDetailDate()
        val error = when {
            editor.description.isBlank() -> "Informe a descrição."
            amount <= 0 -> "Informe um valor maior que zero."
            purchaseDate == null -> "Informe uma data de compra válida."
            dueDate == null -> "Informe uma data de vencimento válida."
            editor.paymentMethod == ExpensePaymentMethod.CARD && editor.cardId == null -> "Selecione o cartão."
            else -> null
        }
        if (error != null) { updateEditor { copy(errorMessage = error) }; return }
        viewModelScope.launch {
            val updated = entity.copy(
                description = editor.description.trim(), amountCents = amount, categoryId = editor.categoryId,
                purchaseDateEpochDay = requireNotNull(purchaseDate).toEpochDay(),
                dueDateEpochDay = requireNotNull(dueDate).toEpochDay(),
                referenceMonth = YearMonth.from(dueDate).year * 100 + YearMonth.from(dueDate).monthValue,
                paymentMethod = editor.paymentMethod.storedValue,
                cardId = editor.cardId.takeIf { editor.paymentMethod == ExpensePaymentMethod.CARD },
            )
            expenseRepository.update(updated)
            currentEntity = updated
            mutableState.update { it.copy(editor = null, message = "Despesa atualizada.") }
            refresh(showLoading = false)
        }
    }

    fun requestDelete() = mutableState.update { it.copy(showDeleteConfirmation = true) }
    fun dismissDelete() = mutableState.update { it.copy(showDeleteConfirmation = false) }
    fun confirmDelete(scope: ExpenseDeleteScope) {
        val entity = currentEntity ?: return
        viewModelScope.launch {
            val disabledAt = clock.nowMillis()
            val success = if (scope == ExpenseDeleteScope.THIS_AND_FUTURE && entity.installmentPlanId != null) {
                disableInstallmentsFrom(entity.installmentPlanId, requireNotNull(entity.installmentNumber), disabledAt) > 0
            } else expenseRepository.disable(entity.id, disabledAt)
            mutableState.update { it.copy(showDeleteConfirmation = false, deleted = success, message = if (success) null else "Não foi possível excluir a despesa.") }
        }
    }

    fun confirmPayment() {
        val id = expenseId ?: return
        viewModelScope.launch {
            val changed = expenseRepository.markAsPaid(id, clock.nowMillis())
            mutableState.update {
                it.copy(
                    showPaymentConfirmation = false,
                    message = if (changed) "Pagamento registrado." else "Não foi possível registrar o pagamento.",
                )
            }
            if (changed) refresh(showLoading = false)
        }
    }

    private suspend fun refresh(showLoading: Boolean = true) {
        if (showLoading) mutableState.update { it.copy(isLoading = true, message = null) }
        val entity = expenseId?.let { expenseRepository.findById(it) }
        if (entity == null || !entity.enabled) {
            mutableState.update { it.copy(isLoading = false, expense = null, message = "Despesa não encontrada.") }
            return
        }
        currentEntity = entity
        val category = categoryRepository.findById(entity.categoryId)?.name ?: "Categoria indisponível"
        val card = entity.cardId?.let { cardRepository.findById(it)?.name ?: "Cartão indisponível" }
        mutableState.update { it.copy(isLoading = false, expense = entity.toUi(category, card, clock)) }
    }

    private fun updateEditor(transform: ExpenseEditUi.() -> ExpenseEditUi) {
        mutableState.update { it.copy(editor = it.editor?.transform()) }
    }
}

private fun ExpenseEntryEntity.toUi(category: String, card: String?, clock: AppClock): ExpenseDetailUi {
    val dueDate = LocalDate.ofEpochDay(dueDateEpochDay)
    return ExpenseDetailUi(
        id, description, amountCents, category,
        LocalDate.ofEpochDay(purchaseDateEpochDay), dueDate,
        paymentMethod.toPaymentMethodLabel(), card, origin.toOriginLabel(),
        installmentNumber?.let { "$it/${installmentCount ?: it}" },
        installmentPlanId != null,
        paidAt?.let { Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDateTime() },
        when {
            paidAt != null -> PaymentStatus.PAID
            dueDate.isBefore(clock.today()) -> PaymentStatus.OVERDUE
            else -> PaymentStatus.PENDING
        },
    )
}

private val DetailDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
private fun String.toDetailDate() = runCatching { LocalDate.parse(this, DetailDateFormatter) }.getOrNull()
private fun formatEditableMoney(cents: Long) = "%d,%02d".format(cents / 100, cents % 100)
private fun String.toPaymentMethodLabel() = when (this) {
    "CARD" -> "Cartão de crédito"
    "PIX" -> "Pix"
    "CASH" -> "Dinheiro"
    "DEBIT" -> "Débito"
    else -> lowercase().replaceFirstChar { it.uppercase() }
}
private fun String.toOriginLabel() = when (this) {
    "EVENTUAL" -> "Eventual"
    "INSTALLMENT" -> "Parcelada"
    "RECURRING" -> "Recorrente"
    else -> lowercase().replaceFirstChar { it.uppercase() }
}
