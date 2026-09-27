package br.com.arthiviatech.myfinances.ui.future

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.arthiviatech.myfinances.data.repository.CardRepository
import br.com.arthiviatech.myfinances.data.repository.CategoryRepository
import br.com.arthiviatech.myfinances.data.repository.ExpenseRepository
import br.com.arthiviatech.myfinances.data.repository.RecurringExpenseRepository
import br.com.arthiviatech.myfinances.core.AppClock
import br.com.arthiviatech.myfinances.domain.usecase.ConfirmRecurringExpenseForMonthUseCase
import br.com.arthiviatech.myfinances.domain.usecase.PauseRecurringExpenseUseCase
import br.com.arthiviatech.myfinances.domain.usecase.ResolveDateForMonthUseCase
import br.com.arthiviatech.myfinances.domain.usecase.ResumeRecurringExpenseUseCase
import br.com.arthiviatech.myfinances.ui.expense.parseMoneyToCents
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

private val FutureDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

class FutureViewModel(
    private val recurringRepository: RecurringExpenseRepository,
    private val expenseRepository: ExpenseRepository,
    categoryRepository: CategoryRepository,
    cardRepository: CardRepository,
    private val confirmRecurring: ConfirmRecurringExpenseForMonthUseCase,
    private val pauseRecurring: PauseRecurringExpenseUseCase,
    private val resumeRecurring: ResumeRecurringExpenseUseCase,
    private val resolveDate: ResolveDateForMonthUseCase,
    private val clock: AppClock,
) : ViewModel() {
    private val controls = MutableStateFlow(FutureUiState())
    private val refresh = MutableStateFlow(0)

    val uiState = combine(
        controls,
        refresh,
        recurringRepository.observeEnabled(),
        categoryRepository.observeAll(),
        cardRepository.observeAll(),
    ) { state, _, recurrences, categories, cards ->
        val referenceMonth = state.selectedMonth.toReferenceMonth()
        val categoryNames = categories.associate { it.id to it.name }
        val cardNames = cards.associate { it.id to it.name }
        val applicable = recurrences.filter { it.startMonth <= referenceMonth }.mapNotNull { recurrence ->
            val pausedForMonth = recurringRepository.findPauses(recurrence.id).any { pause ->
                referenceMonth >= pause.pausedAt.toReferenceMonth() &&
                    (pause.resumedAt == null || referenceMonth < (pause.resumesFromMonth ?: Int.MAX_VALUE))
            }
            val confirmed = expenseRepository.findByRecurrenceAndMonth(recurrence.id, referenceMonth) != null
            if (confirmed) return@mapNotNull null
            FutureAccountUi(
                recurrence.id, recurrence.description,
                categoryNames[recurrence.categoryId] ?: "Categoria indisponível",
                recurrence.dueDay, recurrence.paymentMethod.toLabel(),
                recurrence.cardId?.let { cardNames[it] ?: "Cartão indisponível" },
                pausedForMonth,
            )
        }
        state.copy(pending = applicable.filterNot { it.paused }, paused = applicable.filter { it.paused })
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FutureUiState())

    fun previousMonth() = update { copy(selectedMonth = selectedMonth.minusMonths(1), message = null) }
    fun nextMonth() = update { copy(selectedMonth = selectedMonth.plusMonths(1), message = null) }
    fun selectAccount(account: FutureAccountUi) = update { copy(selectedAccount = account) }
    fun dismissActions() = update { copy(selectedAccount = null) }

    fun openValueEditor(account: FutureAccountUi) {
        val dueDate = resolveDate(controls.value.selectedMonth, account.dueDay)
        update { copy(selectedAccount = null, editor = FutureValueEditorUi(account, dueDate = dueDate.format(FutureDateFormatter))) }
    }
    fun dismissEditor() = update { copy(editor = null) }
    fun openAccountEditor() {
        val account = controls.value.selectedAccount ?: return
        update { copy(selectedAccount = null, accountEditor = FutureAccountEditorUi(account)) }
    }
    fun dismissAccountEditor() = update { copy(accountEditor = null) }
    fun updateAccountDescription(value: String) = updateAccountEditor { copy(description = value, errorMessage = null) }
    fun updateAccountDueDay(value: String) = updateAccountEditor { copy(dueDay = value.filter(Char::isDigit), errorMessage = null) }
    fun saveAccountEditor() {
        val editor = controls.value.accountEditor ?: return
        val dueDay = editor.dueDay.toIntOrNull()
        val error = when {
            editor.description.isBlank() -> "Informe uma descrição."
            dueDay !in 1..31 -> "O dia deve estar entre 1 e 31."
            else -> null
        }
        if (error != null) {
            updateAccountEditor { copy(errorMessage = error) }
            return
        }
        viewModelScope.launch {
            runCatching {
                val current = requireNotNull(recurringRepository.findById(editor.account.id))
                recurringRepository.update(current.copy(description = editor.description.trim(), dueDay = dueDay!!))
            }.onSuccess {
                update { copy(accountEditor = null, message = "Conta futura atualizada.") }
                refresh.update { it + 1 }
            }.onFailure {
                updateAccountEditor { copy(errorMessage = it.message ?: "Não foi possível atualizar a conta.") }
            }
        }
    }
    fun updateAmount(value: String) = updateEditor { copy(amount = value, errorMessage = null) }
    fun updateDueDate(value: String) = updateEditor { copy(dueDate = formatDateInput(value), errorMessage = null) }

    fun confirmValue() {
        val editor = controls.value.editor ?: return
        val amount = parseMoneyToCents(editor.amount)
        val date = runCatching { LocalDate.parse(editor.dueDate, FutureDateFormatter) }.getOrNull()
        val month = controls.value.selectedMonth
        val error = when {
            amount <= 0 -> "Informe um valor maior que zero."
            date == null -> "Informe uma data válida."
            YearMonth.from(date) != month -> "O vencimento deve pertencer ao mês selecionado."
            else -> null
        }
        if (error != null) { updateEditor { copy(errorMessage = error) }; return }
        viewModelScope.launch {
            runCatching { confirmRecurring(editor.account.id, month.toReferenceMonth(), amount, clock.today(), date) }
                .onSuccess { update { copy(editor = null, message = "Despesa lançada no mês.") }; refresh.update { it + 1 } }
                .onFailure { updateEditor { copy(errorMessage = it.message ?: "Não foi possível lançar a despesa.") } }
        }
    }

    fun requestAction(action: FutureAccountAction) {
        val account = controls.value.selectedAccount ?: return
        update { copy(selectedAccount = null, confirmation = FutureActionConfirmationUi(account, action)) }
    }
    fun dismissConfirmation() = update { copy(confirmation = null) }
    fun confirmAction() {
        val confirmation = controls.value.confirmation ?: return
        viewModelScope.launch {
            runCatching {
                when (confirmation.action) {
                    FutureAccountAction.PAUSE -> pauseRecurring(confirmation.account.id, clock.nowMillis())
                    FutureAccountAction.RESUME -> resumeRecurring(confirmation.account.id, clock.nowMillis(), controls.value.selectedMonth.toReferenceMonth())
                    FutureAccountAction.END -> recurringRepository.disable(confirmation.account.id, clock.nowMillis())
                }
            }.onSuccess { update { copy(confirmation = null) }; refresh.update { it + 1 } }
                .onFailure { update { copy(confirmation = null, message = it.message ?: "Não foi possível concluir a ação.") } }
        }
    }
    fun clearMessage() = update { copy(message = null) }
    private fun update(transform: FutureUiState.() -> FutureUiState) = controls.update(transform)
    private fun updateEditor(transform: FutureValueEditorUi.() -> FutureValueEditorUi) = update { copy(editor = editor?.transform()) }
    private fun updateAccountEditor(transform: FutureAccountEditorUi.() -> FutureAccountEditorUi) = update { copy(accountEditor = accountEditor?.transform()) }
}

private fun YearMonth.toReferenceMonth() = year * 100 + monthValue
private fun Long.toReferenceMonth(): Int {
    val date = java.time.Instant.ofEpochMilli(this).atZone(java.time.ZoneId.systemDefault()).toLocalDate()
    return date.year * 100 + date.monthValue
}
private fun String.toLabel() = when (this) { "CARD" -> "Cartão de crédito"; "PIX" -> "Pix"; "CASH" -> "Dinheiro"; "DEBIT" -> "Débito"; else -> this }
