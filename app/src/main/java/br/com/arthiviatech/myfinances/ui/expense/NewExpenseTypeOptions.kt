package br.com.arthiviatech.myfinances.ui.expense

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.arthiviatech.myfinances.ui.theme.MyFinancesTheme

@Composable
internal fun NewExpenseTypeOptions(uiState: NewExpenseUiState, actions: NewExpenseActions) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ExpenseSwitchRow(
            title = "Compra parcelada",
            description = "Crie todas as parcelas automaticamente.",
            checked = uiState.isInstallment,
            onCheckedChange = actions.onInstallmentChange,
        )
        if (uiState.isInstallment) {
            InstallmentFields(uiState, actions)
        }
        ExpenseSwitchRow(
            title = "Conta recorrente",
            description = "O valor será atualizado antes de cada lançamento.",
            checked = uiState.isRecurring,
            onCheckedChange = actions.onRecurringChange,
        )
    }
}

@Composable
private fun ExpenseSwitchRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text(
                    description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}

@Composable
private fun InstallmentFields(uiState: NewExpenseUiState, actions: NewExpenseActions) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = uiState.installmentCount,
            onValueChange = actions.onInstallmentCountChange,
            label = { Text("Parcelas") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.weight(0.8f),
        )
        OutlinedTextField(
            value = uiState.installmentAmount,
            onValueChange = actions.onInstallmentAmountChange,
            label = { Text("Valor da parcela") },
            prefix = { Text("R$ ") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.weight(1.2f),
        )
    }
}

@Preview(name = "Opções de lançamento", group = "Componentes", showBackground = true, widthDp = 390)
@Composable
private fun NewExpenseTypeOptionsPreview() {
    MyFinancesTheme {
        NewExpenseTypeOptions(
            uiState = previewNewExpenseUiState().copy(
                isInstallment = true,
                installmentCount = "10",
                installmentAmount = "350,00",
            ),
            actions = NewExpenseActions(),
        )
    }
}
