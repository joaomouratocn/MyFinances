package br.com.arthiviatech.myfinances.ui.more

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.arthiviatech.myfinances.ui.theme.MyFinancesTheme
import br.com.arthiviatech.myfinances.ui.tour.tourTarget

@Composable
internal fun MoreManagementSection(
    onRevenues: () -> Unit,
    onCards: () -> Unit,
    onInvoices: () -> Unit,
    onCategories: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        MoreSectionTitle("CADASTROS")
        MoreManagementItem(
            "Receitas", "Fixas e eventuais", Icons.Rounded.Payments, onRevenues,
            modifier = Modifier.tourTarget("more_management"),
        )
        MoreManagementItem("Cartões", "Cartões usados nas despesas", Icons.Rounded.CreditCard, onCards)
        MoreManagementItem("Faturas", "Gastos mensais por cartão", Icons.AutoMirrored.Rounded.ReceiptLong, onInvoices)
        MoreManagementItem("Categorias", "Organização dos lançamentos", Icons.Rounded.Category, onCategories)
    }
}

@Composable
private fun MoreManagementItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(onClick = onClick, modifier = modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Column(modifier = Modifier.weight(1f).padding(horizontal = 16.dp)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null)
        }
    }
}

@Composable
internal fun MoreSectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
    )
}

@Preview(name = "Cadastros", group = "Componentes", showBackground = true, widthDp = 390)
@Composable
private fun MoreManagementSectionPreview() {
    MyFinancesTheme {
        Column(Modifier.padding(16.dp)) {
            MoreManagementSection(onRevenues = {}, onCards = {}, onInvoices = {}, onCategories = {})
        }
    }
}
