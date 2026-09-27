package br.com.arthiviatech.myfinances.ui.reports

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.TrendingDown
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.arthiviatech.myfinances.ui.theme.MyFinancesTheme

@Composable
internal fun ReportsSummary(state: ReportsUiState) {
    BoxWithConstraints {
        if (maxWidth >= 600.dp) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SummaryCard("Receitas", state.revenueCents, Icons.AutoMirrored.Rounded.TrendingUp, MaterialTheme.colorScheme.primary, Modifier.weight(1f))
                SummaryCard("Despesas", state.expenseCents, Icons.AutoMirrored.Rounded.TrendingDown, MaterialTheme.colorScheme.secondary, Modifier.weight(1f))
                SummaryCard("Saldo previsto", state.balanceCents, Icons.Rounded.AccountBalanceWallet, balanceColor(state.balanceCents), Modifier.weight(1f))
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SummaryCard("Receitas", state.revenueCents, Icons.AutoMirrored.Rounded.TrendingUp, MaterialTheme.colorScheme.primary)
                SummaryCard("Despesas", state.expenseCents, Icons.AutoMirrored.Rounded.TrendingDown, MaterialTheme.colorScheme.secondary)
                SummaryCard("Saldo previsto", state.balanceCents, Icons.Rounded.AccountBalanceWallet, balanceColor(state.balanceCents))
            }
        }
    }
}

@Composable
private fun SummaryCard(label: String, amount: Long, icon: ImageVector, accent: Color, modifier: Modifier = Modifier) {
    Card(modifier, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)) {
        Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(formatReportCurrency(amount), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = accent)
            }
            Icon(icon, null, tint = accent)
        }
    }
}

@Composable
private fun balanceColor(amount: Long) = if (amount >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error

@Preview(name = "Resumo mensal", showBackground = true, widthDp = 390)
@Composable
private fun ReportsSummaryPreview() { MyFinancesTheme { ReportsSummary(previewReportsState()) } }
