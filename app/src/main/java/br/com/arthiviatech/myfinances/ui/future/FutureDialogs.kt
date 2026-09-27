package br.com.arthiviatech.myfinances.ui.future

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
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
internal fun FutureValueDialog(editor: FutureValueEditorUi, actions: FutureActions) {
    AlertDialog(
        onDismissRequest = actions.onEditorDismiss,
        title = { Text("Lançar ${editor.account.description}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    editor.amount, actions.onAmountChange, Modifier.fillMaxWidth(),
                    label = { Text("Valor deste mês") }, prefix = { Text("R$ ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true,
                )
                OutlinedTextField(
                    editor.dueDate, actions.onDueDateChange, Modifier.fillMaxWidth(),
                    label = { Text("Vencimento") }, supportingText = { Text("Formato: dd/mm/aaaa") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii), singleLine = true,
                )
                editor.errorMessage?.let { Text(it, color = androidx.compose.material3.MaterialTheme.colorScheme.error) }
            }
        },
        confirmButton = { Button(actions.onValueConfirm) { Text("Confirmar lançamento") } },
        dismissButton = { TextButton(actions.onEditorDismiss) { Text("Cancelar") } },
    )
}

@Composable
internal fun FutureActionsDialog(account: FutureAccountUi, actions: FutureActions) {
    AlertDialog(
        onDismissRequest = actions.onActionsDismiss,
        title = { Text(account.description) },
        text = {
            Column {
                TextButton(actions.onEditAccount, Modifier.fillMaxWidth()) {
                    Icon(Icons.Rounded.Edit, null)
                    Text("Editar dados")
                }
                if (account.paused) {
                    ListItem(
                        headlineContent = { Text("Reativar") },
                        supportingContent = { Text("Volta a partir do mês selecionado") },
                        leadingContent = { Icon(Icons.Rounded.PlayArrow, null) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    TextButton(actions.onResume, Modifier.fillMaxWidth()) { Text("Reativar conta") }
                } else {
                    ListItem(
                        headlineContent = { Text("Pausar") },
                        supportingContent = { Text("Interrompe novas pendências") },
                        leadingContent = { Icon(Icons.Rounded.Pause, null) },
                    )
                    TextButton(actions.onPause, Modifier.fillMaxWidth()) { Text("Pausar conta") }
                }
                ListItem(
                    headlineContent = { Text("Encerrar") },
                    supportingContent = { Text("Preserva os lançamentos anteriores") },
                    leadingContent = { Icon(Icons.Rounded.DeleteOutline, null) },
                )
                TextButton(actions.onEnd, Modifier.fillMaxWidth()) { Text("Encerrar recorrência") }
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(actions.onActionsDismiss) { Text("Fechar") } },
    )
}

@Composable
internal fun FutureAccountEditDialog(editor: FutureAccountEditorUi, actions: FutureActions) {
    AlertDialog(
        onDismissRequest = actions.onAccountEditorDismiss,
        title = { Text("Editar conta futura") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = editor.description,
                    onValueChange = actions.onAccountDescriptionChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Descrição") },
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    singleLine = true,
                )
                OutlinedTextField(
                    value = editor.dueDay,
                    onValueChange = actions.onAccountDueDayChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Dia de vencimento") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                )
                editor.errorMessage?.let { Text(it, color = androidx.compose.material3.MaterialTheme.colorScheme.error) }
            }
        },
        confirmButton = { Button(actions.onAccountEditorSave) { Text("Salvar") } },
        dismissButton = { TextButton(actions.onAccountEditorDismiss) { Text("Cancelar") } },
    )
}

@Composable
internal fun FutureConfirmationDialog(item: FutureActionConfirmationUi, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    val (title, text, action) = when (item.action) {
        FutureAccountAction.PAUSE -> Triple("Pausar ${item.account.description}?", "Novas pendências ficarão suspensas até a reativação.", "Pausar")
        FutureAccountAction.RESUME -> Triple("Reativar ${item.account.description}?", "A recorrência voltará a partir do mês selecionado, sem criar lançamentos retroativos.", "Reativar")
        FutureAccountAction.END -> Triple("Encerrar ${item.account.description}?", "A recorrência não criará novas pendências. O histórico será preservado.", "Encerrar")
    }
    AlertDialog(
        onDismissRequest = onDismiss, title = { Text(title) }, text = { Text(text) },
        confirmButton = { Button(onConfirm) { Text(action) } },
        dismissButton = { TextButton(onDismiss) { Text("Cancelar") } },
    )
}

@Preview(name = "Informar valor", showBackground = true)
@Composable private fun FutureValueDialogPreview() { MyFinancesTheme { FutureValueDialog(FutureValueEditorUi(previewFutureAccount(), dueDate = "10/09/2026"), FutureActions()) } }
