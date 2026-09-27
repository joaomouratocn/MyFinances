package br.com.arthiviatech.myfinances.ui.expense

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.arthiviatech.myfinances.ui.theme.MyFinancesTheme

@Composable
internal fun NewExpenseBasicFields(uiState: NewExpenseUiState, actions: NewExpenseActions) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = uiState.description,
            onValueChange = actions.onDescriptionChange,
            label = { Text("Descrição") },
            placeholder = { Text("Ex.: Supermercado") },
            leadingIcon = { Icon(Icons.Rounded.Description, contentDescription = null) },
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = uiState.amount,
            onValueChange = actions.onAmountChange,
            label = { Text("Valor") },
            placeholder = { Text("0,00") },
            prefix = { Text("R$ ") },
            leadingIcon = { Icon(Icons.Rounded.AttachMoney, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            enabled = !uiState.isRecurring,
            supportingText = if (uiState.isRecurring) {
                { Text("O valor será informado em cada mês.") }
            } else {
                null
            },
            modifier = Modifier.fillMaxWidth(),
        )
        NewExpenseSelector(
            label = "Selecionar categoria",
            selectedLabel = uiState.categories.firstOrNull { it.id == uiState.categoryId }?.label,
            options = uiState.categories,
            onSelected = actions.onCategorySelected,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview(name = "Dados principais", group = "Componentes", showBackground = true, widthDp = 390)
@Composable
private fun NewExpenseBasicFieldsPreview() {
    MyFinancesTheme {
        NewExpenseBasicFields(previewNewExpenseUiState(), NewExpenseActions())
    }
}
