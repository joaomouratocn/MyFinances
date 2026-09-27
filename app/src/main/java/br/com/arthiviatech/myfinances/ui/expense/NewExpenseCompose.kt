package br.com.arthiviatech.myfinances.ui.expense

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.arthiviatech.myfinances.ui.theme.MyFinancesTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun NewExpenseCompose(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NewExpenseViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.saved) {
        if (uiState.saved) {
            viewModel.consumeSaved()
            onSaved()
        }
    }

    NewExpenseContent(
        uiState = uiState,
        actions = NewExpenseActions(
            onBack = onBack,
            onDescriptionChange = viewModel::updateDescription,
            onAmountChange = viewModel::updateAmount,
            onCategorySelected = viewModel::selectCategory,
            onPurchaseDateChange = viewModel::updatePurchaseDate,
            onDueDateChange = viewModel::updateDueDate,
            onPaymentMethodSelected = viewModel::selectPaymentMethod,
            onCardSelected = viewModel::selectCard,
            onCardDueMonthChange = viewModel::updateCardDueMonth,
            onInstallmentChange = viewModel::setInstallment,
            onInstallmentCountChange = viewModel::updateInstallmentCount,
            onInstallmentAmountChange = viewModel::updateInstallmentAmount,
            onRecurringChange = viewModel::setRecurring,
            onSave = viewModel::save,
        ),
        modifier = modifier,
    )
}

data class NewExpenseActions(
    val onBack: () -> Unit = {},
    val onDescriptionChange: (String) -> Unit = {},
    val onAmountChange: (String) -> Unit = {},
    val onCategorySelected: (Long) -> Unit = {},
    val onPurchaseDateChange: (String) -> Unit = {},
    val onDueDateChange: (String) -> Unit = {},
    val onPaymentMethodSelected: (ExpensePaymentMethod) -> Unit = {},
    val onCardSelected: (Long) -> Unit = {},
    val onCardDueMonthChange: (String) -> Unit = {},
    val onInstallmentChange: (Boolean) -> Unit = {},
    val onInstallmentCountChange: (String) -> Unit = {},
    val onInstallmentAmountChange: (String) -> Unit = {},
    val onRecurringChange: (Boolean) -> Unit = {},
    val onSave: () -> Unit = {},
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewExpenseContent(
    uiState: NewExpenseUiState,
    actions: NewExpenseActions,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Nova despesa") },
                navigationIcon = {
                    IconButton(onClick = actions.onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Voltar")
                    }
                },
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Button(
                    onClick = actions.onSave,
                    enabled = !uiState.isSaving,
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .imePadding()
                        .padding(16.dp),
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp).padding(end = 4.dp),
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Icon(Icons.Rounded.Save, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                    }
                    Text(if (uiState.isSaving) "Salvando" else "Salvar despesa")
                }
            }
        },
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
        ) {
            if (maxWidth >= 700.dp) {
                NewExpenseWideForm(uiState, actions)
            } else {
                NewExpenseCompactForm(uiState, actions)
            }
        }
    }
}

@Composable
private fun NewExpenseCompactForm(uiState: NewExpenseUiState, actions: NewExpenseActions) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        NewExpenseError(uiState.errorMessage)
        NewExpenseBasicFields(uiState, actions)
        NewExpenseDateFields(uiState, actions)
        NewExpensePaymentFields(uiState, actions)
        NewExpenseTypeOptions(uiState, actions)
    }
}

@Composable
private fun NewExpenseWideForm(uiState: NewExpenseUiState, actions: NewExpenseActions) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        NewExpenseError(uiState.errorMessage)
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(18.dp)) {
                NewExpenseBasicFields(uiState, actions)
                NewExpenseDateFields(uiState, actions)
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(18.dp)) {
                NewExpensePaymentFields(uiState, actions)
                NewExpenseTypeOptions(uiState, actions)
            }
        }
    }
}

@Composable
private fun NewExpenseError(message: String?) {
    if (message != null) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

@Preview(name = "Nova despesa - Claro", group = "Tela completa", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun NewExpensePreview() {
    MyFinancesTheme {
        NewExpenseContent(previewNewExpenseUiState(), NewExpenseActions())
    }
}

@Preview(name = "Nova despesa - Escuro", group = "Tela completa", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun NewExpenseDarkPreview() {
    MyFinancesTheme(darkTheme = true) {
        NewExpenseContent(previewNewExpenseUiState(), NewExpenseActions())
    }
}

@Preview(name = "Nova despesa - Paisagem", group = "Tela completa", showBackground = true, widthDp = 900, heightDp = 500)
@Composable
private fun NewExpenseLandscapePreview() {
    MyFinancesTheme {
        NewExpenseContent(previewNewExpenseUiState(), NewExpenseActions())
    }
}
