package br.com.arthiviatech.myfinances.ui.reports

import java.time.YearMonth

data class ReportGroupUi(val id: Long?, val label: String, val amountCents: Long)

data class ReportsUiState(
    val selectedMonth: YearMonth = YearMonth.now(),
    val revenueCents: Long = 0,
    val expenseCents: Long = 0,
    val balanceCents: Long = 0,
    val categories: List<ReportGroupUi> = emptyList(),
    val cards: List<ReportGroupUi> = emptyList(),
)
