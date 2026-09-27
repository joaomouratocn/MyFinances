package br.com.arthiviatech.myfinances.ui.future

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
import androidx.compose.material.icons.rounded.EventRepeat
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.PauseCircle
import androidx.compose.material3.Button
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

@Composable
internal fun FutureAccountsList(
    pending: List<FutureAccountUi>, paused: List<FutureAccountUi>,
    onEnterValue: (FutureAccountUi) -> Unit, onAccountClick: (FutureAccountUi) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier, contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        if (pending.isNotEmpty()) {
            item {
                Card(Modifier.fillMaxWidth()) {
                    Text("Atualize os valores abaixo. Caso uma conta não seja mais necessária, encerre a recorrência.", Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onSecondaryContainer)
                }
            }
        }
        item { SectionTitle("Aguardando valor") }
        if (pending.isEmpty()) item { EmptyFutureCard("Nenhuma conta aguardando atualização") }
        items(pending, key = { "pending-${it.id}" }) { AccountCard(it, onEnterValue, onAccountClick) }
        if (paused.isNotEmpty()) {
            item { SectionTitle("Pausadas", Modifier.padding(top = 12.dp)) }
            items(paused, key = { "paused-${it.id}" }) { AccountCard(it, onEnterValue, onAccountClick) }
        }
    }
}

@Composable
private fun AccountCard(item: FutureAccountUi, onEnterValue: (FutureAccountUi) -> Unit, onActions: (FutureAccountUi) -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(if (item.paused) Icons.Rounded.PauseCircle else Icons.Rounded.EventRepeat, null, tint = if (item.paused) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.secondary)
            Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                Text(item.description, fontWeight = FontWeight.Bold)
                Text("${item.category} • Dia ${item.dueDay}", style = MaterialTheme.typography.bodySmall)
                Text(item.card ?: item.paymentMethod, style = MaterialTheme.typography.bodySmall)
            }
            if (!item.paused) Button(onClick = { onEnterValue(item) }) { Text("Informar") }
            IconButton(onClick = { onActions(item) }) { Icon(Icons.Rounded.MoreVert, "Mais ações") }
        }
    }
}

@Composable private fun SectionTitle(text: String, modifier: Modifier = Modifier) { Text(text, modifier, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
@Composable private fun EmptyFutureCard(text: String) { Card(Modifier.fillMaxWidth()) { Text(text, Modifier.padding(20.dp)) } }

@Preview(name = "Lista de contas futuras", showBackground = true, widthDp = 390, heightDp = 600)
@Composable private fun FutureAccountsListPreview() { MyFinancesTheme { FutureAccountsList(previewFutureState().pending, previewFutureState().paused, {}, {}) } }
