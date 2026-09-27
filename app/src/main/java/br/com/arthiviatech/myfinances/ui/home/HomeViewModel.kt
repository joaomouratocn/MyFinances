package br.com.arthiviatech.myfinances.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.arthiviatech.myfinances.data.repository.ExpenseRepository
import br.com.arthiviatech.myfinances.data.repository.CategoryRepository
import br.com.arthiviatech.myfinances.data.repository.CardRepository
import br.com.arthiviatech.myfinances.data.repository.RecurringExpenseRepository
import br.com.arthiviatech.myfinances.core.AppClock
import br.com.arthiviatech.myfinances.domain.usecase.CalculateMonthlyBalanceUseCase
import br.com.arthiviatech.myfinances.domain.usecase.MaterializeFixedRevenuesUseCase
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.Instant
import java.time.ZoneId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val selectedMonth: YearMonth = YearMonth.now(),
    val revenueCents: Long = 0,
    val expenseCents: Long = 0,
    val balanceCents: Long = 0,
    val pendingAccountsCount: Int = 0,
    val expenses: List<HomeExpenseUi> = emptyList(),
    val searchQuery: String = "",
    val filters: HomeExpenseFilters = HomeExpenseFilters(),
    val categories: List<HomeFilterOption> = emptyList(),
    val cards: List<HomeFilterOption> = emptyList(),
    val showFilters: Boolean = false,
)

data class HomeFilterOption(val id: Long, val label: String)
enum class HomeExpenseSort { DEFAULT, DATE_ASC, DATE_DESC, VALUE_ASC, VALUE_DESC, DESCRIPTION_ASC, DESCRIPTION_DESC }
data class HomeExpenseFilters(
    val startDate: String = "",
    val endDate: String = "",
    val categoryId: Long? = null,
    val cardId: Long? = null,
    val status: ExpenseStatus? = null,
    val sort: HomeExpenseSort = HomeExpenseSort.DEFAULT,
    val errorMessage: String? = null,
) {
    val activeCount: Int get() = listOf(startDate.isNotBlank(), endDate.isNotBlank(), categoryId != null, cardId != null, status != null, sort != HomeExpenseSort.DEFAULT).count { it }
}

data class HomeExpenseUi(
    val id: Long,
    val description: String,
    val amountCents: Long,
    val category: String,
    val categoryId: Long,
    val dueDate: LocalDate,
    val installmentLabel: String? = null,
    val cardLabel: String? = null,
    val cardId: Long? = null,
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
    private val categoryRepository: CategoryRepository,
    private val cardRepository: CardRepository,
    private val recurringExpenseRepository: RecurringExpenseRepository,
    private val clock: AppClock,
) : ViewModel() {
    private val selectedMonth = MutableStateFlow(YearMonth.now())
    private val searchQuery = MutableStateFlow("")
    private val filterControls = MutableStateFlow(HomeExpenseFilters())
    private val showFilters = MutableStateFlow(false)

    val uiState = selectedMonth
        .flatMapLatest { month ->
            val referenceMonth = month.year * 100 + month.monthValue
            val monthData = combine(
                calculateMonthlyBalance(referenceMonth),
                expenseRepository.observeByMonth(referenceMonth),
                categoryRepository.observeAll(),
                cardRepository.observeAll(),
            ) { summary, expenses, categories, cards ->
                val today = clock.today()
                val categoryNames = categories.associate { it.id to it.name }
                val cardNames = cards.associate { it.id to it.name }
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
                            category = categoryNames[expense.categoryId] ?: "Categoria indisponível",
                            categoryId = expense.categoryId,
                            dueDate = dueDate,
                            installmentLabel = expense.installmentNumber?.let { number ->
                                "$number/${expense.installmentCount}"
                            },
                            cardLabel = expense.cardId?.let { cardNames[it] ?: "Cartão indisponível" },
                            cardId = expense.cardId,
                            status = when {
                                expense.paidAt != null -> ExpenseStatus.PAID
                                dueDate.isBefore(today) -> ExpenseStatus.OVERDUE
                                else -> ExpenseStatus.PENDING
                            },
                        )
                    },
                    categories = categories.map { HomeFilterOption(it.id, it.name) },
                    cards = cards.map { HomeFilterOption(it.id, it.name) },
                )
            }
            val pendingCount = combine(
                recurringExpenseRepository.observeEnabled(),
                recurringExpenseRepository.observeAllPauses(),
                expenseRepository.observeByMonth(referenceMonth),
            ) { recurrences, pauses, expenses ->
                val confirmedIds = expenses.mapNotNullTo(mutableSetOf()) { it.recurringExpenseId }
                recurrences.count { recurrence ->
                    recurrence.startMonth <= referenceMonth && recurrence.id !in confirmedIds &&
                        pauses.none { pause ->
                            pause.recurringExpenseId == recurrence.id &&
                                referenceMonth >= pause.pausedAt.toPauseReferenceMonth() &&
                                (pause.resumedAt == null || referenceMonth < (pause.resumesFromMonth ?: Int.MAX_VALUE))
                        }
                }
            }
            val completeMonthData = combine(monthData, pendingCount) { state, count -> state.copy(pendingAccountsCount = count) }
            combine(completeMonthData, searchQuery, filterControls, showFilters) { state, query, filters, show ->
                state.copy(
                    expenses = state.expenses.filterAndSort(query, filters),
                    searchQuery = query,
                    filters = filters,
                    showFilters = show,
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

    fun updateSearch(value: String) { searchQuery.value = value }
    fun openFilters() { showFilters.value = true }
    fun dismissFilters() { showFilters.value = false; filterControls.update { it.copy(errorMessage = null) } }
    fun updateStartDate(value: String) = updateFilters { copy(startDate = value, errorMessage = null) }
    fun updateEndDate(value: String) = updateFilters { copy(endDate = value, errorMessage = null) }
    fun selectCategory(id: Long?) = updateFilters { copy(categoryId = id) }
    fun selectCard(id: Long?) = updateFilters { copy(cardId = id) }
    fun selectStatus(status: ExpenseStatus?) = updateFilters { copy(status = status) }
    fun selectSort(sort: HomeExpenseSort) = updateFilters { copy(sort = sort) }
    fun applyFilters() {
        val filters = filterControls.value
        val start = filters.startDate.toFilterDate()
        val end = filters.endDate.toFilterDate()
        val error = when {
            filters.startDate.isNotBlank() && start == null -> "Informe uma data inicial válida."
            filters.endDate.isNotBlank() && end == null -> "Informe uma data final válida."
            start != null && end != null && start > end -> "A data inicial deve ser anterior à data final."
            else -> null
        }
        if (error != null) updateFilters { copy(errorMessage = error) } else showFilters.value = false
    }
    fun clearFilters() { filterControls.value = HomeExpenseFilters(); showFilters.value = false }

    private fun updateFilters(transform: HomeExpenseFilters.() -> HomeExpenseFilters) = filterControls.update(transform)
}

private fun Long.toPauseReferenceMonth(): Int {
    val date = Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()
    return date.year * 100 + date.monthValue
}

private val HomeFilterDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
private fun String.toFilterDate() = runCatching { LocalDate.parse(this, HomeFilterDateFormatter) }.getOrNull()

internal fun List<HomeExpenseUi>.filterAndSort(query: String, filters: HomeExpenseFilters): List<HomeExpenseUi> {
    val start = filters.startDate.toFilterDate()
    val end = filters.endDate.toFilterDate()
    val filtered = filter { expense ->
        expense.description.contains(query.trim(), ignoreCase = true) &&
            (start == null || !expense.dueDate.isBefore(start)) &&
            (end == null || !expense.dueDate.isAfter(end)) &&
            (filters.categoryId == null || expense.categoryId == filters.categoryId) &&
            (filters.cardId == null || expense.cardId == filters.cardId) &&
            (filters.status == null || expense.status == filters.status)
    }
    val comparator = when (filters.sort) {
        HomeExpenseSort.DEFAULT -> Comparator<HomeExpenseUi> { left, right ->
            val rank = mapOf(ExpenseStatus.OVERDUE to 0, ExpenseStatus.PENDING to 1, ExpenseStatus.PAID to 2)
            val statusComparison = requireNotNull(rank[left.status]).compareTo(requireNotNull(rank[right.status]))
            if (statusComparison != 0) statusComparison
            else if (left.status == ExpenseStatus.PAID) right.dueDate.compareTo(left.dueDate)
            else left.dueDate.compareTo(right.dueDate)
        }
        HomeExpenseSort.DATE_ASC -> compareBy(HomeExpenseUi::dueDate)
        HomeExpenseSort.DATE_DESC -> compareByDescending(HomeExpenseUi::dueDate)
        HomeExpenseSort.VALUE_ASC -> compareBy(HomeExpenseUi::amountCents)
        HomeExpenseSort.VALUE_DESC -> compareByDescending(HomeExpenseUi::amountCents)
        HomeExpenseSort.DESCRIPTION_ASC -> compareBy(String.CASE_INSENSITIVE_ORDER) { it.description }
        HomeExpenseSort.DESCRIPTION_DESC -> compareByDescending<HomeExpenseUi> { it.description.lowercase() }
    }
    return filtered.sortedWith(comparator)
}
