package br.com.arthiviatech.myfinances.ui.revenue

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.EditCalendar
import androidx.compose.material.icons.rounded.EventRepeat
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import br.com.arthiviatech.myfinances.ui.theme.MyFinancesTheme

@Composable
fun FixedRevenueActionsDialog(fixed: FixedRevenueUi, actions: RevenueActions) {
    AlertDialog(
        onDismissRequest = actions.onFixedActionsDismiss,
        title = { Text(fixed.description) },
        text = {
            Column {
                ListItem(
                    headlineContent = { Text("Editar somente este mês") },
                    supportingContent = { Text("Altera o valor ou a data desta ocorrência") },
                    leadingContent = { Icon(Icons.Rounded.EditCalendar, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().clickable(onClick = actions.onEditCurrentMonth),
                )
                if (fixed.isActive) {
                    ListItem(
                        headlineContent = { Text("Editar meses futuros") },
                        supportingContent = { Text("Cria um novo padrão a partir deste mês") },
                        leadingContent = { Icon(Icons.Rounded.EventRepeat, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().clickable(onClick = actions.onEditFuture),
                    )
                    ListItem(
                        headlineContent = { Text("Desativar", color = MaterialTheme.colorScheme.error) },
                        leadingContent = { Icon(Icons.Rounded.DeleteOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                        modifier = Modifier.fillMaxWidth().clickable(onClick = actions.onFixedDisable),
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = actions.onFixedActionsDismiss) { Text("Fechar") } },
    )
}

@Preview(name = "Ações da receita fixa", showBackground = true)
@Composable
private fun FixedRevenueActionsDialogPreview() {
    MyFinancesTheme { FixedRevenueActionsDialog(previewRevenueUiState().fixedRevenues.first(), RevenueActions()) }
}
