package br.com.arthiviatech.myfinances.ui.expense

import java.time.LocalDate
import java.time.LocalDateTime

internal fun previewExpenseDetail(paid: Boolean = false) = ExpenseDetailUi(
    id = 1,
    description = "Energia elétrica",
    amountCents = 23890,
    category = "Moradia",
    purchaseDate = LocalDate.of(2026, 9, 1),
    dueDate = LocalDate.of(2026, 9, 15),
    paymentMethod = "Pix",
    card = null,
    origin = "Eventual",
    installmentLabel = null,
    isInstallment = false,
    paidAt = if (paid) LocalDateTime.of(2026, 9, 12, 18, 42) else null,
    status = if (paid) PaymentStatus.PAID else PaymentStatus.PENDING,
)

internal fun previewExpenseDetailState(paid: Boolean = false) =
    ExpenseDetailUiState(isLoading = false, expense = previewExpenseDetail(paid))
