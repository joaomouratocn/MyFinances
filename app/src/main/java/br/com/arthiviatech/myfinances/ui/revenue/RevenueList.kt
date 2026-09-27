package br.com.arthiviatech.myfinances.ui.revenue

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.EventRepeat
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.arthiviatech.myfinances.ui.theme.MyFinancesTheme
import java.text.NumberFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
internal fun RevenueMonthSelector(
    month: YearMonth,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        IconButton(onClick = onPrevious) {
            Icon(Icons.Rounded.ChevronLeft, contentDescription = "Mês anterior")
        }
        Text(
            text = month.format(DateTimeFormatter.ofPattern("MMMM 'de' yyyy", Locale.forLanguageTag("pt-BR")))
                .replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        IconButton(onClick = onNext) {
            Icon(Icons.Rounded.ChevronRight, contentDescription = "Próximo mês")
        }
    }
}

@Composable
internal fun RevenueList(
    fixedRevenues: List<FixedRevenueUi>,
    monthlyEntries: List<RevenueEntryUi>,
    onFixedClick: (FixedRevenueUi) -> Unit,
    onEntryEdit: (RevenueEntryUi) -> Unit,
    onEntryDisable: (RevenueEntryUi) -> Unit,
    modifier: Modifier = Modifier,
) {
    val eventualEntries = monthlyEntries.filterNot { it.isFixed }
    LazyColumn(
        modifier = modifier,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(start = 16.dp, end = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item { RevenueSectionTitle("Fixas") }
        if (fixedRevenues.isEmpty()) {
            item { RevenueEmptyCard("Nenhuma receita fixa cadastrada") }
        } else {
            items(fixedRevenues, key = { "fixed-${it.id}" }) { item ->
                FixedRevenueCard(item, onClick = { onFixedClick(item) })
            }
        }
        item { RevenueSectionTitle("Eventuais do mês", Modifier.padding(top = 14.dp)) }
        if (eventualEntries.isEmpty()) {
            item { RevenueEmptyCard("Nenhuma receita eventual neste mês") }
        } else {
            items(eventualEntries, key = { "entry-${it.id}" }) { item ->
                EventualRevenueCard(item, onEntryEdit, onEntryDisable)
            }
        }
    }
}

@Composable
private fun RevenueSectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(text, modifier = modifier, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
}

@Composable
private fun FixedRevenueCard(item: FixedRevenueUi, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.EventRepeat, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Column(modifier = Modifier.weight(1f).padding(horizontal = 12.dp)) {
                Text(item.description, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(
                    "Prevista para o dia ${item.expectedDay} • Ativa",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(formatRevenueCurrency(item.amountCents), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun EventualRevenueCard(
    item: RevenueEntryUi,
    onEdit: (RevenueEntryUi) -> Unit,
    onDisable: (RevenueEntryUi) -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(start = 16.dp, top = 10.dp, bottom = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.AutoMirrored.Rounded.TrendingUp, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Column(modifier = Modifier.weight(1f).padding(horizontal = 12.dp)) {
                Text(item.description, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(
                    LocalDate.ofEpochDay(item.expectedDateEpochDay).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(formatRevenueCurrency(item.amountCents), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
            IconButton(onClick = { onEdit(item) }) {
                Icon(Icons.Rounded.Edit, contentDescription = "Editar ${item.description}")
            }
            IconButton(onClick = { onDisable(item) }) {
                Icon(Icons.Rounded.DeleteOutline, contentDescription = "Desativar ${item.description}", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun RevenueEmptyCard(text: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = text,
            modifier = Modifier.padding(20.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

internal fun formatRevenueCurrency(cents: Long): String =
    NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(cents / 100.0)

@Preview(name = "Listas de receitas", group = "Componentes", showBackground = true, widthDp = 390, heightDp = 600)
@Composable
private fun RevenueListPreview() {
    MyFinancesTheme {
        RevenueList(
            fixedRevenues = previewFixedRevenues(),
            monthlyEntries = previewRevenueEntries(),
            onFixedClick = {},
            onEntryEdit = {},
            onEntryDisable = {},
        )
    }
}
