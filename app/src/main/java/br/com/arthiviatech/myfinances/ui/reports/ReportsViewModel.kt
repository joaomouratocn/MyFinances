package br.com.arthiviatech.myfinances.ui.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.arthiviatech.myfinances.data.repository.CardRepository
import br.com.arthiviatech.myfinances.data.repository.CategoryRepository
import br.com.arthiviatech.myfinances.data.repository.ReportRepository
import br.com.arthiviatech.myfinances.domain.usecase.MaterializeFixedRevenuesUseCase
import java.time.YearMonth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ReportsViewModel(
    reportRepository: ReportRepository,
    categoryRepository: CategoryRepository,
    cardRepository: CardRepository,
    private val materializeFixedRevenues: MaterializeFixedRevenuesUseCase,
) : ViewModel() {
    private val selectedMonth = MutableStateFlow(YearMonth.now())

    val uiState = selectedMonth.flatMapLatest { month ->
        combine(
            reportRepository.observeMonthlySummary(month.toReferenceMonth()),
            categoryRepository.observeAll(),
            cardRepository.observeAll(),
        ) { summary, categories, cards ->
            val categoryNames = categories.associate { it.id to it.name }
            val cardNames = cards.associate { it.id to it.name }
            ReportsUiState(
                selectedMonth = month,
                revenueCents = summary.revenueCents,
                expenseCents = summary.expenseCents,
                balanceCents = summary.balanceCents,
                categories = summary.expenseByCategoryCents.map { (id, amount) ->
                    ReportGroupUi(id, categoryNames[id] ?: "Categoria indisponível", amount)
                }.sortedByDescending(ReportGroupUi::amountCents),
                cards = summary.expenseByCardCents.map { (id, amount) ->
                    ReportGroupUi(id, id?.let { cardNames[it] } ?: "Outras formas de pagamento", amount)
                }.sortedByDescending(ReportGroupUi::amountCents),
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ReportsUiState())

    init {
        viewModelScope.launch {
            selectedMonth.collect { month -> runCatching { materializeFixedRevenues(month.toReferenceMonth()) } }
        }
    }

    fun previousMonth() { selectedMonth.value = selectedMonth.value.minusMonths(1) }
    fun nextMonth() { selectedMonth.value = selectedMonth.value.plusMonths(1) }
}

private fun YearMonth.toReferenceMonth() = year * 100 + monthValue
