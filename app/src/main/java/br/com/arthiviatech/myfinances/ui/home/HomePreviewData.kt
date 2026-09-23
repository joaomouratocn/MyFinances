package br.com.arthiviatech.myfinances.ui.home

import java.time.LocalDate
import java.time.YearMonth

internal fun previewHomeUiState() = HomeUiState(
    selectedMonth = YearMonth.of(2026, 9),
    revenueCents = 500_000,
    expenseCents = 255_000,
    balanceCents = 245_000,
    pendingAccountsCount = 3,
    expenses = previewHomeExpenses(),
)

internal fun previewHomeExpenses() = listOf(
    HomeExpenseUi(
        id = 1,
        description = "Aluguel",
        amountCents = 120_000,
        category = "Moradia",
        dueDate = LocalDate.of(2026, 9, 10),
        status = ExpenseStatus.PENDING,
    ),
    HomeExpenseUi(
        id = 2,
        description = "Notebook",
        amountCents = 35_000,
        category = "Compras",
        dueDate = LocalDate.of(2026, 9, 15),
        installmentLabel = "3/10",
        cardLabel = "Cartão A",
        status = ExpenseStatus.PAID,
    ),
    HomeExpenseUi(
        id = 3,
        description = "Energia elétrica",
        amountCents = 18_650,
        category = "Moradia",
        dueDate = LocalDate.of(2026, 9, 5),
        status = ExpenseStatus.OVERDUE,
    ),
)
