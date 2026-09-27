package br.com.arthiviatech.myfinances.ui.expense

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.arthiviatech.myfinances.ui.theme.MyFinancesTheme

@Composable
internal fun NewExpenseDateFields(uiState: NewExpenseUiState, actions: NewExpenseActions) {
    val selectedCard = uiState.cards.firstOrNull { it.id == uiState.cardId }
    val automaticDueDate = uiState.paymentMethod == ExpensePaymentMethod.CARD
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ExpenseDateField(
                value = uiState.purchaseDate,
                onValueChange = actions.onPurchaseDateChange,
                label = "Compra",
                modifier = Modifier.weight(1f),
            )
            ExpenseDateField(
                value = uiState.dueDate,
                onValueChange = actions.onDueDateChange,
                label = if (automaticDueDate) "Vencimento calculado" else "Vencimento",
                enabled = !automaticDueDate,
                modifier = Modifier.weight(1f),
            )
        }
        if (automaticDueDate && selectedCard?.closingDay == null) {
            OutlinedTextField(
                value = uiState.cardDueMonth,
                onValueChange = actions.onCardDueMonthChange,
                label = { Text("Mês do primeiro vencimento") },
                placeholder = { Text("mm/aaaa") },
                supportingText = { Text("Necessário porque o cartão não possui dia de fechamento") },
                trailingIcon = { Icon(Icons.Rounded.CalendarMonth, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        } else if (automaticDueDate && selectedCard != null) {
            Text(
                "Calculado pelo fechamento no dia ${selectedCard.closingDay} e vencimento no dia ${selectedCard.dueDay}.",
                style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ExpenseDateField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text("dd/mm/aaaa") },
        trailingIcon = { Icon(Icons.Rounded.CalendarMonth, contentDescription = null) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
        enabled = enabled,
        singleLine = true,
        modifier = modifier,
    )
}

@Preview(name = "Datas", group = "Componentes", showBackground = true, widthDp = 390)
@Composable
private fun NewExpenseDateFieldsPreview() {
    MyFinancesTheme {
        NewExpenseDateFields(previewNewExpenseUiState(), NewExpenseActions())
    }
}
