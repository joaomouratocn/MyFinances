package br.com.arthiviatech.myfinances.ui.revenue

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import br.com.arthiviatech.myfinances.ui.theme.MyFinancesTheme

@Composable
fun RevenueTypeDialog(
    onFixedClick: () -> Unit,
    onEventualClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Qual é o tipo da receita?") },
        text = {
            Column {
                ListItem(
                    headlineContent = { Text("Receita fixa") },
                    supportingContent = { Text("Repete todos os meses") },
                    leadingContent = { Icon(Icons.Rounded.CalendarMonth, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().clickable(onClick = onFixedClick),
                )
                ListItem(
                    headlineContent = { Text("Receita eventual") },
                    supportingContent = { Text("Acontece somente uma vez") },
                    leadingContent = { Icon(Icons.Rounded.Payments, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().clickable(onClick = onEventualClick),
                )
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
    )
}

@Preview(name = "Tipo de receita", showBackground = true)
@Composable
private fun RevenueTypeDialogPreview() {
    MyFinancesTheme { RevenueTypeDialog({}, {}, {}) }
}
