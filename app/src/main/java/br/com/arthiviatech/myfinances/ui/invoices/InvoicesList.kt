package br.com.arthiviatech.myfinances.ui.invoices

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.arthiviatech.myfinances.ui.expense.PaymentStatus
import br.com.arthiviatech.myfinances.ui.expense.formatExpenseCurrency
import br.com.arthiviatech.myfinances.ui.expense.paymentSuccessColor
import br.com.arthiviatech.myfinances.ui.theme.MyFinancesTheme
import java.time.format.DateTimeFormatter

@Composable
internal fun InvoicesList(state: InvoicesUiState, onExpenseClick: (Long) -> Unit, modifier: Modifier = Modifier) {
    if (state.invoices.isEmpty()) {
        Column(modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(Icons.Rounded.CreditCard, null, tint = MaterialTheme.colorScheme.primary)
            Text("Nenhuma despesa de cartão neste mês", Modifier.padding(top = 12.dp), style = MaterialTheme.typography.titleMedium)
        }
        return
    }
    LazyColumn(
        modifier,
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item { InvoiceGrandTotal(state.totalCents) }
        items(state.invoices, key = CardInvoiceUi::cardId) { invoice -> InvoiceCard(invoice, onExpenseClick) }
    }
}

@Composable
private fun InvoiceGrandTotal(total: Long) {
    Card(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth().padding(18.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Total em cartões", fontWeight = FontWeight.SemiBold)
            Text(formatExpenseCurrency(total), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
        }
    }
}

@Composable
private fun InvoiceCard(invoice: CardInvoiceUi, onExpenseClick: (Long) -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column {
            Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.CreditCard, null, tint = MaterialTheme.colorScheme.primary)
                Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                    Text(invoice.cardName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("${invoice.expenses.size} ${if (invoice.expenses.size == 1) "despesa" else "despesas"}")
                }
                Text(formatExpenseCurrency(invoice.totalCents), fontWeight = FontWeight.Bold)
            }
            invoice.expenses.forEach { expense ->
                HorizontalDivider()
                InvoiceExpenseRow(expense, onExpenseClick)
            }
        }
    }
}

@Composable
private fun InvoiceExpenseRow(expense: InvoiceExpenseUi, onClick: (Long) -> Unit) {
    Surface(onClick = { onClick(expense.id) }, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            val statusData = when (expense.status) {
                PaymentStatus.PENDING -> Icons.Rounded.Schedule to MaterialTheme.colorScheme.secondary
                PaymentStatus.OVERDUE -> Icons.Rounded.Error to MaterialTheme.colorScheme.error
                PaymentStatus.PAID -> Icons.Rounded.CheckCircle to paymentSuccessColor()
            }
            Icon(statusData.first, contentDescription = expense.status.name, tint = statusData.second)
            Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                Text(expense.description, fontWeight = FontWeight.SemiBold)
                Text(
                    "Vence em ${expense.dueDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}" + (expense.installmentLabel?.let { " • $it" } ?: ""),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(formatExpenseCurrency(expense.amountCents), fontWeight = FontWeight.SemiBold)
        }
    }
}

@Preview(name = "Lista de faturas", showBackground = true, widthDp = 390, heightDp = 650)
@Composable
private fun InvoicesListPreview() { MyFinancesTheme { InvoicesList(previewInvoicesState(), {}) } }
