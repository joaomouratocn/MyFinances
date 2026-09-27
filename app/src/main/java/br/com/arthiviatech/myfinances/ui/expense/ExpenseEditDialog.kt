package br.com.arthiviatech.myfinances.ui.expense

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.arthiviatech.myfinances.ui.theme.MyFinancesTheme

@Composable
internal fun ExpenseEditDialog(
    editor: ExpenseEditUi,
    categories: List<ExpenseOptionUi>,
    cards: List<ExpenseOptionUi>,
    actions: ExpenseDetailActions,
) {
    AlertDialog(
        onDismissRequest = actions.onEditorDismiss,
        title = { Text("Editar despesa") },
        text = {
            Column(
                Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedTextField(editor.description, actions.onDescriptionChange, Modifier.fillMaxWidth(), label = { Text("Descrição") }, keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences), singleLine = true)
                OutlinedTextField(
                    editor.amount, actions.onAmountChange, Modifier.fillMaxWidth(), label = { Text("Valor") }, prefix = { Text("R$ ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true,
                )
                NewExpenseSelector(
                    "Categoria",
                    categories.firstOrNull { it.id == editor.categoryId }?.label ?: editor.currentCategoryLabel,
                    categories,
                    actions.onCategorySelected,
                    Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    editor.purchaseDate, actions.onPurchaseDateChange, Modifier.fillMaxWidth(),
                    label = { Text("Data da compra") }, supportingText = { Text("Formato: dd/mm/aaaa") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii), singleLine = true,
                )
                OutlinedTextField(
                    editor.dueDate, actions.onDueDateChange, Modifier.fillMaxWidth(),
                    label = { Text("Vencimento") }, supportingText = { Text("Formato: dd/mm/aaaa") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii), singleLine = true,
                )
                NewExpenseSelector(
                    "Forma de pagamento", editor.paymentMethod.label,
                    ExpensePaymentMethod.entries.map { ExpenseOptionUi(it.ordinal.toLong(), it.label) },
                    { actions.onPaymentMethodSelected(ExpensePaymentMethod.entries[it.toInt()]) }, Modifier.fillMaxWidth(),
                )
                if (editor.paymentMethod == ExpensePaymentMethod.CARD) {
                    NewExpenseSelector(
                        "Cartão", cards.firstOrNull { it.id == editor.cardId }?.label ?: editor.currentCardLabel,
                        cards, actions.onCardSelected, Modifier.fillMaxWidth(),
                    )
                }
                editor.errorMessage?.let { Text(it, color = androidx.compose.material3.MaterialTheme.colorScheme.error) }
            }
        },
        confirmButton = { TextButton(actions.onEditorSave) { Text("Salvar") } },
        dismissButton = { TextButton(actions.onEditorDismiss) { Text("Cancelar") } },
    )
}

@Preview(name = "Editar despesa", showBackground = true)
@Composable
private fun ExpenseEditDialogPreview() {
    MyFinancesTheme {
        ExpenseEditDialog(
            ExpenseEditUi("Supermercado", "250,00", 1, "05/09/2026", "10/09/2026", ExpensePaymentMethod.CARD, 1, "Alimentação", "Cartão principal"),
            listOf(ExpenseOptionUi(1, "Alimentação")), listOf(ExpenseOptionUi(1, "Cartão principal")), ExpenseDetailActions(),
        )
    }
}
