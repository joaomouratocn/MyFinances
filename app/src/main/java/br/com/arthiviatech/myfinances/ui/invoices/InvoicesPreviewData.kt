package br.com.arthiviatech.myfinances.ui.invoices

import br.com.arthiviatech.myfinances.ui.expense.PaymentStatus
import java.time.LocalDate
import java.time.YearMonth

internal fun previewInvoicesState() = InvoicesUiState(
    selectedMonth = YearMonth.of(2026, 9),
    totalCents = 187850,
    invoices = listOf(
        CardInvoiceUi(
            1, "Cartão principal", 132850,
            listOf(
                InvoiceExpenseUi(1, "Supermercado", 82850, LocalDate.of(2026, 9, 10), null, PaymentStatus.PAID),
                InvoiceExpenseUi(2, "Assinatura", 50000, LocalDate.of(2026, 9, 10), "3/12", PaymentStatus.PENDING),
            ),
        ),
        CardInvoiceUi(2, "Compras", 55000, listOf(InvoiceExpenseUi(3, "Pet shop", 55000, LocalDate.of(2026, 9, 22), null, PaymentStatus.OVERDUE))),
    ),
)
