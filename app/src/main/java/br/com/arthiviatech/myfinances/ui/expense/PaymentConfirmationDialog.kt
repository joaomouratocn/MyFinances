package br.com.arthiviatech.myfinances.ui.expense

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import br.com.arthiviatech.myfinances.ui.theme.MyFinancesTheme

@Composable
internal fun PaymentConfirmationDialog(expense: ExpenseDetailUi?, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    if (expense == null) return
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirmar pagamento?") },
        text = { Text("Registrar ${expense.description}, no valor de ${formatExpenseCurrency(expense.amountCents)}, como paga agora?") },
        confirmButton = { Button(onClick = onConfirm) { Text("Confirmar pagamento") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
    )
}

@Preview(name = "Confirmação de pagamento", showBackground = true)
@Composable
private fun PaymentConfirmationPreview() { MyFinancesTheme { PaymentConfirmationDialog(previewExpenseDetail(), {}, {}) } }
