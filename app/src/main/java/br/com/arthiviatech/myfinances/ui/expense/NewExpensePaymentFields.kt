package br.com.arthiviatech.myfinances.ui.expense

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.arthiviatech.myfinances.ui.theme.MyFinancesTheme

@Composable
internal fun NewExpensePaymentFields(uiState: NewExpenseUiState, actions: NewExpenseActions) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        NewExpenseSelector(
            label = "Forma de pagamento",
            selectedLabel = uiState.paymentMethod.label,
            options = ExpensePaymentMethod.entries.map { method ->
                ExpenseOptionUi(method.ordinal.toLong(), method.label)
            },
            onSelected = { ordinal ->
                actions.onPaymentMethodSelected(ExpensePaymentMethod.entries[ordinal.toInt()])
            },
            modifier = Modifier.fillMaxWidth(),
        )
        if (uiState.paymentMethod == ExpensePaymentMethod.CARD) {
            NewExpenseSelector(
                label = "Selecionar cartão",
                selectedLabel = uiState.cards.firstOrNull { it.id == uiState.cardId }?.label,
                options = uiState.cards,
                onSelected = actions.onCardSelected,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Preview(name = "Pagamento com cartão", group = "Componentes", showBackground = true, widthDp = 390)
@Composable
private fun NewExpensePaymentFieldsPreview() {
    MyFinancesTheme {
        NewExpensePaymentFields(
            uiState = previewNewExpenseUiState().copy(paymentMethod = ExpensePaymentMethod.CARD, cardId = 1),
            actions = NewExpenseActions(),
        )
    }
}
