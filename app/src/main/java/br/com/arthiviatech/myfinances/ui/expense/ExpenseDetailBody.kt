package br.com.arthiviatech.myfinances.ui.expense

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.arthiviatech.myfinances.ui.theme.MyFinancesTheme
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
internal fun ExpenseDetailBody(expense: ExpenseDetailUi, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PaymentStatusBadge(expense.status)
                Text(expense.description, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(formatExpenseCurrency(expense.amountCents), style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.secondary)
            }
        }
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                DetailRow("Categoria", expense.category)
                DetailRow("Compra", expense.purchaseDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                DetailRow("Vencimento", expense.dueDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                DetailRow("Forma de pagamento", expense.paymentMethod)
                expense.card?.let { DetailRow("Cartão", it) }
                DetailRow("Tipo", expense.origin)
                expense.installmentLabel?.let { DetailRow("Parcela", it) }
            }
        }
        expense.paidAt?.let {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Pagamento registrado", fontWeight = FontWeight.Bold, color = paymentSuccessColor())
                    Text(it.format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm")))
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun PaymentStatusBadge(status: PaymentStatus) {
    val presentation = when (status) {
        PaymentStatus.PENDING -> Triple("Pendente", Icons.Rounded.Schedule, MaterialTheme.colorScheme.secondary)
        PaymentStatus.OVERDUE -> Triple("Atrasada", Icons.Rounded.Error, MaterialTheme.colorScheme.error)
        PaymentStatus.PAID -> Triple("Paga", Icons.Rounded.CheckCircle, paymentSuccessColor())
    }
    StatusSurface(presentation.first, presentation.second, presentation.third)
}

@Composable
private fun StatusSurface(label: String, icon: ImageVector, color: Color) {
    Surface(color = color.copy(alpha = .12f), shape = MaterialTheme.shapes.extraLarge) {
        Row(Modifier.padding(horizontal = 10.dp, vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = color)
            Text(label, Modifier.padding(start = 6.dp), color = color, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
internal fun paymentSuccessColor() = if (isSystemInDarkTheme()) Color(0xFF7DDA95) else Color(0xFF146C2E)

internal fun formatExpenseCurrency(cents: Long): String =
    NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(cents / 100.0)

@Preview(name = "Detalhes da despesa", showBackground = true, widthDp = 390, heightDp = 650)
@Composable
private fun ExpenseDetailBodyPreview() { MyFinancesTheme { ExpenseDetailBody(previewExpenseDetail()) } }
