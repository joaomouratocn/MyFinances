package br.com.arthiviatech.myfinances.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.arthiviatech.myfinances.data.repository.ExpenseRepository
import br.com.arthiviatech.myfinances.domain.usecase.CalculateMonthlyBalanceUseCase
import br.com.arthiviatech.myfinances.domain.usecase.MaterializeFixedRevenuesUseCase
import java.time.LocalDate
import java.time.YearMonth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val selectedMonth: YearMonth = YearMonth.now(),
    val revenueCents: Long = 0,
    val expenseCents: Long = 0,
    val balanceCents: Long = 0,
    val pendingAccountsCount: Int = 0,
    val expenses: List<HomeExpenseUi> = emptyList(),
)

data class HomeExpenseUi(
    val id: Long,
    val description: String,
    val amountCents: Long,
    val category: String,
    val dueDate: LocalDate,
    val installmentLabel: String? = null,
    val cardLabel: String? = null,
    val status: ExpenseStatus,
)

enum class ExpenseStatus {
    PENDING,
    PAID,
    OVERDUE,
}

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val expenseRepository: ExpenseRepository,
    private val calculateMonthlyBalance: CalculateMonthlyBalanceUseCase,
    private val materializeFixedRevenues: MaterializeFixedRevenuesUseCase,
) : ViewModel() {
    private val selectedMonth = MutableStateFlow(YearMonth.now())

    val uiState = selectedMonth
        .flatMapLatest { month ->
            val referenceMonth = month.year * 100 + month.monthValue
            combine(
                calculateMonthlyBalance(referenceMonth),
                expenseRepository.observeByMonth(referenceMonth),
            ) { summary, expenses ->
                val today = LocalDate.now()
                HomeUiState(
                    selectedMonth = month,
                    revenueCents = summary.revenueCents,
                    expenseCents = summary.expenseCents,
                    balanceCents = summary.balanceCents,
                    expenses = expenses.map { expense ->
                        val dueDate = LocalDate.ofEpochDay(expense.dueDateEpochDay)
                        HomeExpenseUi(
                            id = expense.id,
                            description = expense.description,
                            amountCents = expense.amountCents,
                            category = "Categoria",
                            dueDate = dueDate,
                            installmentLabel = expense.installmentNumber?.let { number ->
                                "$number/${expense.installmentCount}"
                            },
                            cardLabel = expense.cardId?.let { "Cartão" },
                            status = when {
                                expense.paidAt != null -> ExpenseStatus.PAID
                                dueDate.isBefore(today) -> ExpenseStatus.OVERDUE
                                else -> ExpenseStatus.PENDING
                            },
                        )
                    },
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState(),
        )

    init {
        viewModelScope.launch {
            selectedMonth.collect { month ->
                val referenceMonth = month.year * 100 + month.monthValue
                runCatching { materializeFixedRevenues(referenceMonth) }
            }
        }
    }

    fun selectPreviousMonth() {
        selectedMonth.value = selectedMonth.value.minusMonths(1)
    }

    fun selectNextMonth() {
        selectedMonth.value = selectedMonth.value.plusMonths(1)
    }
}
