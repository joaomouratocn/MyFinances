package br.com.arthiviatech.myfinances.ui.reports

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.arthiviatech.myfinances.ui.theme.MyFinancesTheme

@Composable
internal fun ReportBreakdownSection(title: String, items: List<ReportGroupUi>) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Card(Modifier.fillMaxWidth()) {
            if (items.isEmpty()) {
                Text("Nenhuma despesa neste mês", modifier = Modifier.fillMaxWidth().padding(16.dp))
            } else {
                val total = items.sumOf { it.amountCents }.coerceAtLeast(1)
                Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    items.forEach { item -> BreakdownRow(item, total) }
                }
            }
        }
    }
}

@Composable
private fun BreakdownRow(item: ReportGroupUi, total: Long) {
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Text(item.label, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
            Text(formatReportCurrency(item.amountCents), fontWeight = FontWeight.SemiBold)
        }
        LinearProgressIndicator(progress = { item.amountCents.toFloat() / total }, modifier = Modifier.fillMaxWidth())
    }
}

@Preview(name = "Agrupamento", showBackground = true, widthDp = 390)
@Composable
private fun ReportBreakdownPreview() { MyFinancesTheme { ReportBreakdownSection("Despesas por categoria", previewReportsState().categories) } }
