package br.com.arthiviatech.myfinances.ui.revenue

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.arthiviatech.myfinances.ui.theme.MyFinancesTheme

@Composable
fun RevenueEditorDialog(
    editor: RevenueEditorUi,
    categories: List<RevenueOptionUi>,
    actions: RevenueActions,
) {
    val usesDay = editor.mode == RevenueEditorMode.CREATE_FIXED || editor.mode == RevenueEditorMode.EDIT_FUTURE
    val descriptionEnabled = editor.mode != RevenueEditorMode.EDIT_FUTURE &&
        !(editor.mode == RevenueEditorMode.EDIT_MONTH && editor.fixedRevenueId != null)

    AlertDialog(
        onDismissRequest = actions.onEditorDismiss,
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
                if (editor.mode != RevenueEditorMode.EDIT_FUTURE) {
                    RevenueCategorySelector(categories, editor.categoryId, actions.onCategorySelected)
                }
                editor.errorMessage?.let { Text(it, color = androidx.compose.material3.MaterialTheme.colorScheme.error) }
            }
        },
        confirmButton = { TextButton(onClick = actions.onEditorSave) { Text("Salvar") } },
        dismissButton = { TextButton(onClick = actions.onEditorDismiss) { Text("Cancelar") } },
    )
}

@Composable
private fun RevenueCategorySelector(
    categories: List<RevenueOptionUi>,
    selectedId: Long?,
    onSelected: (Long?) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val label = categories.firstOrNull { it.id == selectedId }?.label ?: "Sem categoria"
    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
            Text(label, modifier = Modifier.weight(1f))
            Icon(Icons.Rounded.ArrowDropDown, contentDescription = "Selecionar categoria")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text("Sem categoria") },
                onClick = { onSelected(null); expanded = false },
            )
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category.label) },
                    onClick = { onSelected(category.id); expanded = false },
                )
            }
        }
    }
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
            categories = previewRevenueUiState().categories,
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
            categories = previewRevenueUiState().categories,
            actions = RevenueActions(),
        )
    }
}
