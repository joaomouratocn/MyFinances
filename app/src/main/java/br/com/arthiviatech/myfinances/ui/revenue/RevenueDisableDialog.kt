package br.com.arthiviatech.myfinances.ui.revenue

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import br.com.arthiviatech.myfinances.ui.theme.MyFinancesTheme

@Composable
fun RevenueDisableDialog(
    target: RevenueDisableTarget,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val detail = when (target) {
        is RevenueDisableTarget.Fixed -> "Ela não será criada nos próximos meses. O histórico será mantido."
        is RevenueDisableTarget.Entry -> "Este lançamento será removido da lista. O histórico será mantido."
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Desativar ${target.description}?") },
        text = { Text(detail) },
        confirmButton = { TextButton(onClick = onConfirm) { Text("Desativar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
    )
}

@Preview(name = "Desativar receita", showBackground = true)
@Composable
private fun RevenueDisableDialogPreview() {
    MyFinancesTheme {
        RevenueDisableDialog(RevenueDisableTarget.Fixed(1, "Salário"), {}, {})
    }
}
