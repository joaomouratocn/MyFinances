package br.com.arthiviatech.myfinances.ui.expense

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import br.com.arthiviatech.myfinances.ui.theme.MyFinancesTheme

@Composable
internal fun ExpenseDeleteDialog(expense: ExpenseDetailUi?, actions: ExpenseDetailActions) {
    if (expense == null) return
    AlertDialog(
        onDismissRequest = actions.onDeleteDismiss,
        title = { Text("Excluir ${expense.description}?") },
        text = {
            if (expense.isInstallment) {
                Column {
                    Text("Escolha quais parcelas devem ser desativadas. Os registros anteriores serão preservados.")
                    TextButton(actions.onDeleteOnlyThis, Modifier.fillMaxWidth()) { Text("Somente esta parcela") }
                    Button(actions.onDeleteThisAndFuture, Modifier.fillMaxWidth()) { Text("Esta e as futuras") }
                }
            } else {
                Text("A despesa será desativada e deixará de aparecer nas consultas comuns.")
            }
        },
        confirmButton = {
            if (!expense.isInstallment) TextButton(actions.onDeleteOnlyThis) { Text("Excluir") }
        },
        dismissButton = { TextButton(actions.onDeleteDismiss) { Text("Cancelar") } },
    )
}

@Preview(name = "Excluir parcela", showBackground = true)
@Composable
private fun ExpenseDeleteDialogPreview() {
    MyFinancesTheme { ExpenseDeleteDialog(previewExpenseDetail().copy(isInstallment = true, installmentLabel = "3/12"), ExpenseDetailActions()) }
}
