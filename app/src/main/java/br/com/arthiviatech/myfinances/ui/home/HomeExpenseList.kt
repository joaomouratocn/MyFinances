package br.com.arthiviatech.myfinances.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.arthiviatech.myfinances.ui.theme.MyFinancesTheme
import java.time.format.DateTimeFormatter

@Composable
internal fun HomeExpenseList(
    expenses: List<HomeExpenseUi>,
    onExpenseClick: (Long) -> Unit,
) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "Despesas do mês",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
        )
        AssistChip(onClick = {}, label = { Text("Filtrar") })
    }

    if (expenses.isEmpty()) {
        HomeEmptyExpenses()
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            expenses.forEach { expense ->
                HomeExpenseCard(expense = expense, onClick = { onExpenseClick(expense.id) })
            }
        }
    }
}

@Composable
private fun HomeEmptyExpenses() {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = Icons.Rounded.Payments,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp),
            )
            Text("Nenhuma despesa neste mês", fontWeight = FontWeight.Bold)
            Text(
                text = "Use “Nova despesa” para criar seu primeiro lançamento.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun HomeExpenseCard(expense: HomeExpenseUi, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), onClick = onClick) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = buildString {
                        append(expense.description)
                        expense.installmentLabel?.let { append(" $it") }
                    },
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = formatCurrency(expense.amountCents),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = buildList {
                        add(expense.category)
                        expense.cardLabel?.let(::add)
                        add("vence ${expense.dueDate.format(DateTimeFormatter.ofPattern("dd/MM"))}")
                    }.joinToString(" • "),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                HomeExpenseStatusBadge(expense.status)
            }
        }
    }
}

@Composable
private fun HomeExpenseStatusBadge(status: ExpenseStatus) {
    val (label, icon, color) = when (status) {
        ExpenseStatus.PENDING -> Triple("Pendente", Icons.Rounded.Schedule, MaterialTheme.colorScheme.secondary)
        ExpenseStatus.PAID -> Triple("Paga", Icons.Rounded.CheckCircle, HomeSuccess)
        ExpenseStatus.OVERDUE -> Triple("Atrasada", Icons.Rounded.Error, MaterialTheme.colorScheme.error)
    }

    Surface(color = color.copy(alpha = 0.12f), shape = MaterialTheme.shapes.extraLarge) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(4.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, color = color)
        }
    }
}

@Preview(
    name = "Lista de despesas",
    group = "Componentes",
    showBackground = true,
    widthDp = 390,
    heightDp = 420,
)
@Composable
private fun HomeExpenseListPreview() {
    MyFinancesTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            HomeExpenseList(
                expenses = previewHomeExpenses(),
                onExpenseClick = {},
            )
        }
    }
}

@Preview(name = "Lista vazia", group = "Estados", showBackground = true, widthDp = 390)
@Composable
private fun HomeEmptyExpenseListPreview() {
    MyFinancesTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            HomeExpenseList(expenses = emptyList(), onExpenseClick = {})
        }
    }
}
