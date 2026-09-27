package br.com.arthiviatech.myfinances.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.arthiviatech.myfinances.ui.theme.MyFinancesTheme

@Composable
internal fun HomeExpenseFilterDialog(state: HomeUiState, actions: HomeFilterActions) {
    AlertDialog(
        onDismissRequest = actions.onDismissFilters,
        title = { Text("Filtrar e ordenar") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterDateField("De", state.filters.startDate, actions.onStartDateChange, Modifier.weight(1f))
                    FilterDateField("Até", state.filters.endDate, actions.onEndDateChange, Modifier.weight(1f))
                }
                FilterSelector(
                    "Todas as categorias",
                    state.categories.firstOrNull { it.id == state.filters.categoryId }?.label,
                    listOf(null to "Todas as categorias") + state.categories.map { it.id to it.label },
                    actions.onCategorySelected,
                )
                FilterSelector(
                    "Todos os cartões",
                    state.cards.firstOrNull { it.id == state.filters.cardId }?.label,
                    listOf(null to "Todos os cartões") + state.cards.map { it.id to it.label },
                    actions.onCardSelected,
                )
                FilterSelector(
                    "Todas as situações",
                    state.filters.status?.label(),
                    listOf(null to "Todas as situações") + ExpenseStatus.entries.map { it to it.label() },
                    actions.onStatusSelected,
                )
                FilterSelector(
                    "Ordenação padrão",
                    state.filters.sort.label(),
                    HomeExpenseSort.entries.map { it to it.label() },
                    actions.onSortSelected,
                )
                state.filters.errorMessage?.let { Text(it, color = androidx.compose.material3.MaterialTheme.colorScheme.error) }
            }
        },
        confirmButton = { TextButton(actions.onApplyFilters) { Text("Aplicar") } },
        dismissButton = {
            Row {
                TextButton(actions.onClearFilters) { Text("Limpar") }
                TextButton(actions.onDismissFilters) { Text("Cancelar") }
            }
        },
    )
}

@Composable
private fun FilterDateField(label: String, value: String, onChange: (String) -> Unit, modifier: Modifier) {
    OutlinedTextField(
        value, onChange, modifier, label = { Text(label) }, placeholder = { Text("dd/mm/aaaa") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true,
    )
}

@Composable
private fun <T> FilterSelector(placeholder: String, selectedLabel: String?, options: List<Pair<T, String>>, onSelected: (T) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box(Modifier.fillMaxWidth()) {
        OutlinedButton({ expanded = true }, Modifier.fillMaxWidth()) {
            Text(selectedLabel ?: placeholder, Modifier.weight(1f))
            Icon(Icons.Rounded.ArrowDropDown, null)
        }
        DropdownMenu(expanded, { expanded = false }) {
            options.forEach { (value, label) ->
                DropdownMenuItem({ Text(label) }, onClick = { onSelected(value); expanded = false })
            }
        }
    }
}

private fun ExpenseStatus.label() = when (this) { ExpenseStatus.PENDING -> "Pendente"; ExpenseStatus.OVERDUE -> "Atrasada"; ExpenseStatus.PAID -> "Paga" }
private fun HomeExpenseSort.label() = when (this) {
    HomeExpenseSort.DEFAULT -> "Ordenação padrão"
    HomeExpenseSort.DATE_ASC -> "Data: mais antiga"
    HomeExpenseSort.DATE_DESC -> "Data: mais recente"
    HomeExpenseSort.VALUE_ASC -> "Valor: menor primeiro"
    HomeExpenseSort.VALUE_DESC -> "Valor: maior primeiro"
    HomeExpenseSort.DESCRIPTION_ASC -> "Descrição: A–Z"
    HomeExpenseSort.DESCRIPTION_DESC -> "Descrição: Z–A"
}

@Preview(name = "Filtros de despesas", showBackground = true)
@Composable
private fun HomeExpenseFilterDialogPreview() {
    MyFinancesTheme {
        HomeExpenseFilterDialog(
            previewHomeUiState().copy(
                showFilters = true,
                categories = listOf(HomeFilterOption(1, "Moradia")),
                cards = listOf(HomeFilterOption(1, "Cartão principal")),
            ),
            HomeFilterActions(),
        )
    }
}
