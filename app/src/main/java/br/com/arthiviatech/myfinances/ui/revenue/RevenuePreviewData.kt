package br.com.arthiviatech.myfinances.ui.revenue

import java.time.LocalDate
import java.time.YearMonth

internal fun previewRevenueUiState() = RevenueUiState(
    selectedMonth = YearMonth.of(2026, 9),
    fixedRevenues = previewFixedRevenues(),
    monthlyEntries = previewRevenueEntries(),
    categories = listOf(
        RevenueOptionUi(1, "Diversos"),
        RevenueOptionUi(2, "Trabalho"),
    ),
)

internal fun previewFixedRevenues() = listOf(
    FixedRevenueUi(1, "Salário", 500_000, 5),
    FixedRevenueUi(2, "Aluguel recebido", 120_000, 10),
)

internal fun previewRevenueEntries() = listOf(
    RevenueEntryUi(
        id = 10,
        fixedRevenueId = null,
        description = "Renda extra",
        amountCents = 40_000,
        expectedDateEpochDay = LocalDate.of(2026, 9, 11).toEpochDay(),
        categoryId = 1,
        isFixed = false,
    ),
)
