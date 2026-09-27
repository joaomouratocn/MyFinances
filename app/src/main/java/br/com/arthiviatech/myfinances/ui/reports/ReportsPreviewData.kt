package br.com.arthiviatech.myfinances.ui.reports

import java.text.NumberFormat
import java.time.YearMonth
import java.util.Locale

internal fun previewReportsState() = ReportsUiState(
    selectedMonth = YearMonth.of(2026, 9),
    revenueCents = 650000,
    expenseCents = 427850,
    balanceCents = 222150,
    categories = listOf(
        ReportGroupUi(1, "Moradia", 210000),
        ReportGroupUi(2, "Alimentação", 122850),
        ReportGroupUi(3, "Compras", 95000),
    ),
    cards = listOf(
        ReportGroupUi(1, "Cartão principal", 187850),
        ReportGroupUi(null, "Outras formas de pagamento", 240000),
    ),
)

internal fun formatReportCurrency(cents: Long): String =
    NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(cents / 100.0)
