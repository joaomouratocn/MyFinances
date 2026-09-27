package br.com.arthiviatech.myfinances.ui.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
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
fun CardEditorDialog(editor: CardEditorUi, actions: CardsActions) {
    AlertDialog(
        onDismissRequest = actions.onEditorDismiss,
        title = { Text(if (editor.cardId == null) "Novo cartão" else "Editar cartão") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    editor.name, actions.onNameChange, Modifier.fillMaxWidth(),
                    label = { Text("Nome") }, singleLine = true, isError = editor.nameError != null,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                    supportingText = editor.nameError?.let { error -> { Text(error) } },
                )
                DayField("Dia de vencimento", editor.dueDay, editor.dueDayError, actions.onDueDayChange)
                DayField("Dia de fechamento (opcional)", editor.closingDay, editor.closingDayError, actions.onClosingDayChange)
            }
        },
        confirmButton = { TextButton(actions.onEditorSave) { Text("Salvar") } },
        dismissButton = { TextButton(actions.onEditorDismiss) { Text("Cancelar") } },
    )
}

@Composable
private fun DayField(label: String, value: String, error: String?, onChange: (String) -> Unit) {
    OutlinedTextField(
        value, onChange, Modifier.fillMaxWidth(), label = { Text(label) }, placeholder = { Text("1 a 31") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true,
        isError = error != null, supportingText = error?.let { message -> { Text(message) } },
    )
}

@Preview(name = "Cadastro de cartão", showBackground = true)
@Composable
private fun CardEditorDialogPreview() {
    MyFinancesTheme { CardEditorDialog(CardEditorUi(name = "Cartão principal", dueDay = "10", closingDay = "3"), CardsActions()) }
}
