package br.com.arthiviatech.myfinances.ui.categories

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import br.com.arthiviatech.myfinances.ui.theme.MyFinancesTheme

@Composable
internal fun CategoryStatusDialog(
    category: CategoryUi,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val action = if (category.enabled) "Desativar" else "Reativar"
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("$action categoria?") },
        text = {
            Text(
                if (category.enabled) {
                    "${category.name} deixará de aparecer em novos lançamentos. O histórico será preservado."
                } else {
                    "${category.name} voltará a aparecer nos novos lançamentos."
                },
            )
        },
        confirmButton = { Button(onClick = onConfirm) { Text(action) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
    )
}

@Preview(name = "Desativar categoria", group = "Diálogos", showBackground = true)
@Composable
private fun CategoryStatusDialogPreview() {
    MyFinancesTheme {
        CategoryStatusDialog(
            category = previewCategories().first(),
            onConfirm = {},
            onDismiss = {},
        )
    }
}
