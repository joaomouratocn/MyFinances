package br.com.arthiviatech.myfinances.ui.invoices

import br.com.arthiviatech.myfinances.ui.expense.PaymentStatus
import java.time.LocalDate
import java.time.YearMonth

data class InvoiceExpenseUi(
    val id: Long,
    val description: String,
    val amountCents: Long,
    val dueDate: LocalDate,
    val installmentLabel: String?,
    val status: PaymentStatus,
)

data class CardInvoiceUi(
    val cardId: Long,
    val cardName: String,
    val totalCents: Long,
    val expenses: List<InvoiceExpenseUi>,
)

data class InvoicesUiState(
    val selectedMonth: YearMonth = YearMonth.now(),
    val totalCents: Long = 0,
    val invoices: List<CardInvoiceUi> = emptyList(),
)
