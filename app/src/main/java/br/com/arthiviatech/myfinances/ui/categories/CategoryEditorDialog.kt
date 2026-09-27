package br.com.arthiviatech.myfinances.ui.categories

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import br.com.arthiviatech.myfinances.ui.theme.MyFinancesTheme

@Composable
internal fun CategoryEditorDialog(
    editor: CategoryEditorUi,
    onNameChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (editor.categoryId == null) "Nova categoria" else "Editar categoria") },
        text = {
            OutlinedTextField(
                value = editor.name,
                onValueChange = onNameChange,
                label = { Text("Nome") },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                singleLine = true,
                isError = editor.errorMessage != null,
                supportingText = editor.errorMessage?.let { message ->
                    { Text(message) }
                },
                modifier = Modifier,
            )
        },
        confirmButton = { Button(onClick = onSave) { Text("Salvar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
    )
}

@Preview(name = "Nova categoria", group = "Diálogos", showBackground = true)
@Composable
private fun CategoryEditorDialogPreview() {
    MyFinancesTheme {
        CategoryEditorDialog(
            editor = CategoryEditorUi(name = "Transporte"),
            onNameChange = {},
            onSave = {},
            onDismiss = {},
        )
    }
}
