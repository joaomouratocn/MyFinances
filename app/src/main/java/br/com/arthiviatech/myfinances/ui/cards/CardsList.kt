package br.com.arthiviatech.myfinances.ui.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.PowerSettingsNew
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
internal fun CardsList(cards: List<CardUi>, filter: CardFilter, onEdit: (CardUi) -> Unit, onStatusChange: (CardUi) -> Unit, modifier: Modifier = Modifier) {
    if (cards.isEmpty()) {
        CardsEmptyState(filter, modifier)
        return
    }
    LazyVerticalGrid(
        columns = GridCells.Adaptive(300.dp),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 96.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(cards, key = { it.id }) { card -> CardItem(card, onEdit, onStatusChange) }
    }
}

@Composable
private fun CardItem(card: CardUi, onEdit: (CardUi) -> Unit, onStatusChange: (CardUi) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 14.dp, bottom = 14.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Rounded.CreditCard, null, tint = if (card.enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
            Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                Text(card.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(
                    "Vence no dia ${card.dueDay}" + (card.closingDay?.let { " • Fecha no dia $it" } ?: ""),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (card.closingDay == null) Text("Sem fechamento definido", style = MaterialTheme.typography.bodySmall)
            }
            if (card.enabled) {
                IconButton({ onEdit(card) }) { Icon(Icons.Rounded.Edit, "Editar ${card.name}") }
            }
            IconButton({ onStatusChange(card) }) {
                Icon(
                    Icons.Rounded.PowerSettingsNew,
                    if (card.enabled) "Desativar ${card.name}" else "Reativar ${card.name}",
                    tint = if (card.enabled) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
private fun CardsEmptyState(filter: CardFilter, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(Icons.Rounded.CreditCard, null, tint = MaterialTheme.colorScheme.primary)
        Text(
            if (filter == CardFilter.ENABLED) "Nenhum cartão cadastrado" else "Nenhum cartão desativado",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 12.dp),
        )
        if (filter == CardFilter.ENABLED) Text("Use Novo cartão para começar")
    }
}

@Preview(name = "Lista de cartões", group = "Componentes", showBackground = true, widthDp = 390, heightDp = 400)
@Composable
private fun CardsListPreview() { MyFinancesTheme { CardsList(previewCards(), CardFilter.ENABLED, {}, {}) } }
