package br.com.arthiviatech.myfinances.ui.invoices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.arthiviatech.myfinances.data.repository.CardRepository
import br.com.arthiviatech.myfinances.data.repository.ExpenseRepository
import br.com.arthiviatech.myfinances.core.AppClock
import br.com.arthiviatech.myfinances.ui.expense.PaymentStatus
import java.time.LocalDate
import java.time.YearMonth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class InvoicesViewModel(
    expenseRepository: ExpenseRepository,
    cardRepository: CardRepository,
    private val clock: AppClock,
) : ViewModel() {
    private val selectedMonth = MutableStateFlow(YearMonth.now())

    val uiState = selectedMonth.flatMapLatest { month ->
        combine(
            expenseRepository.observeByMonth(month.year * 100 + month.monthValue),
            cardRepository.observeAll(),
        ) { expenses, cards ->
            val cardNames = cards.associate { it.id to it.name }
            val cardExpenses = expenses.filter { it.cardId != null }
            val invoices = cardExpenses.groupBy { requireNotNull(it.cardId) }.map { (cardId, entries) ->
                CardInvoiceUi(
                    cardId = cardId,
                    cardName = cardNames[cardId] ?: "Cartão indisponível",
                    totalCents = entries.sumOf { it.amountCents },
                    expenses = entries.map { entry ->
                        val dueDate = LocalDate.ofEpochDay(entry.dueDateEpochDay)
                        InvoiceExpenseUi(
                            id = entry.id,
                            description = entry.description,
                            amountCents = entry.amountCents,
                            dueDate = dueDate,
                            installmentLabel = entry.installmentNumber?.let { "$it/${entry.installmentCount ?: it}" },
                            status = when {
                                entry.paidAt != null -> PaymentStatus.PAID
                                dueDate.isBefore(clock.today()) -> PaymentStatus.OVERDUE
                                else -> PaymentStatus.PENDING
                            },
                        )
                    }.sortedBy(InvoiceExpenseUi::dueDate),
                )
            }.sortedByDescending(CardInvoiceUi::totalCents)
            InvoicesUiState(month, cardExpenses.sumOf { it.amountCents }, invoices)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), InvoicesUiState())

    fun previousMonth() { selectedMonth.value = selectedMonth.value.minusMonths(1) }
    fun nextMonth() { selectedMonth.value = selectedMonth.value.plusMonths(1) }
}
