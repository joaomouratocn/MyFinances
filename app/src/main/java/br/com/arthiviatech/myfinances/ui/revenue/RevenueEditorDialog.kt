package br.com.arthiviatech.myfinances.ui.revenue

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
import androidx.compose.ui.window.DialogProperties
import br.com.arthiviatech.myfinances.ui.theme.MyFinancesTheme

@Composable
fun RevenueEditorDialog(
    editor: RevenueEditorUi,
    actions: RevenueActions,
) {
    val usesDay = editor.mode == RevenueEditorMode.CREATE_FIXED || editor.mode == RevenueEditorMode.EDIT_FUTURE
    val descriptionEnabled = editor.mode != RevenueEditorMode.EDIT_FUTURE &&
        !(editor.mode == RevenueEditorMode.EDIT_MONTH && editor.fixedRevenueId != null)

    AlertDialog(
        onDismissRequest = actions.onEditorDismiss,
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
        ),
        title = { Text(editor.title()) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedTextField(
                    value = editor.description,
                    onValueChange = actions.onDescriptionChange,
                    label = { Text("Descrição") },
                    enabled = descriptionEnabled,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = editor.amount,
                    onValueChange = actions.onAmountChange,
                    label = { Text("Valor") },
                    prefix = { Text("R$ ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                if (usesDay) {
                    OutlinedTextField(
                        value = editor.expectedDay,
                        onValueChange = actions.onExpectedDayChange,
                        label = { Text("Dia previsto") },
                        supportingText = { Text("Use um dia entre 1 e 31") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                } else {
                    OutlinedTextField(
                        value = editor.expectedDate,
                        onValueChange = actions.onExpectedDateChange,
                        label = { Text("Data prevista") },
                        supportingText = { Text("Formato: dd/mm/aaaa") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                editor.errorMessage?.let { Text(it, color = androidx.compose.material3.MaterialTheme.colorScheme.error) }
            }
        },
        confirmButton = { TextButton(onClick = actions.onEditorSave) { Text("Salvar") } },
        dismissButton = { TextButton(onClick = actions.onEditorDismiss) { Text("Cancelar") } },
    )
}

private fun RevenueEditorUi.title() = when (mode) {
    RevenueEditorMode.CREATE_FIXED -> "Nova receita fixa"
    RevenueEditorMode.CREATE_EVENTUAL -> "Nova receita eventual"
    RevenueEditorMode.EDIT_MONTH -> "Editar receita do mês"
    RevenueEditorMode.EDIT_FUTURE -> "Editar meses futuros"
}

@Preview(name = "Editor de receita fixa", showBackground = true)
@Composable
private fun FixedRevenueEditorPreview() {
    MyFinancesTheme {
        RevenueEditorDialog(
            editor = RevenueEditorUi(RevenueEditorMode.CREATE_FIXED, description = "Salário", amount = "5.000,00", expectedDay = "5"),
            actions = RevenueActions(),
        )
    }
}

@Preview(name = "Editor de receita eventual", showBackground = true)
@Composable
private fun EventualRevenueEditorPreview() {
    MyFinancesTheme {
        RevenueEditorDialog(
            editor = RevenueEditorUi(RevenueEditorMode.CREATE_EVENTUAL, expectedDate = "15/09/2026"),
            actions = RevenueActions(),
        )
    }
}
