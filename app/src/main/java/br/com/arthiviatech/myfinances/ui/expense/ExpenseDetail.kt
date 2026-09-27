package br.com.arthiviatech.myfinances.ui.expense

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.arthiviatech.myfinances.ui.theme.MyFinancesTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun ExpenseDetailsCompose(
    expenseId: Long,
    onBack: () -> Unit,
    onDeleted: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ExpenseDetailViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(expenseId) { viewModel.load(expenseId) }
    LaunchedEffect(state.deleted) { if (state.deleted) onDeleted() }
    ExpenseDetailsContent(
        state,
        ExpenseDetailActions(
            onBack, viewModel::requestPayment, viewModel::dismissPayment, viewModel::confirmPayment,
            viewModel::openEditor, viewModel::dismissEditor, viewModel::updateEditDescription,
            viewModel::updateEditAmount, viewModel::selectEditCategory, viewModel::updateEditPurchaseDate,
            viewModel::updateEditDueDate, viewModel::selectEditPaymentMethod, viewModel::selectEditCard,
            viewModel::saveEditor, viewModel::requestDelete, viewModel::dismissDelete,
            { viewModel.confirmDelete(ExpenseDeleteScope.ONLY_THIS) },
            { viewModel.confirmDelete(ExpenseDeleteScope.THIS_AND_FUTURE) }, viewModel::clearMessage,
        ),
        modifier,
    )
}

data class ExpenseDetailActions(
    val onBack: () -> Unit = {},
    val onRequestPayment: () -> Unit = {},
    val onDismissPayment: () -> Unit = {},
    val onConfirmPayment: () -> Unit = {},
    val onEdit: () -> Unit = {},
    val onEditorDismiss: () -> Unit = {},
    val onDescriptionChange: (String) -> Unit = {},
    val onAmountChange: (String) -> Unit = {},
    val onCategorySelected: (Long) -> Unit = {},
    val onPurchaseDateChange: (String) -> Unit = {},
    val onDueDateChange: (String) -> Unit = {},
    val onPaymentMethodSelected: (ExpensePaymentMethod) -> Unit = {},
    val onCardSelected: (Long) -> Unit = {},
    val onEditorSave: () -> Unit = {},
    val onDelete: () -> Unit = {},
    val onDeleteDismiss: () -> Unit = {},
    val onDeleteOnlyThis: () -> Unit = {},
    val onDeleteThisAndFuture: () -> Unit = {},
    val onMessageShown: () -> Unit = {},
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseDetailsContent(state: ExpenseDetailUiState, actions: ExpenseDetailActions, modifier: Modifier = Modifier) {
    val snackbar = remember { SnackbarHostState() }
    LaunchedEffect(state.message) { state.message?.let { snackbar.showSnackbar(it); actions.onMessageShown() } }
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Detalhes da despesa") },
                navigationIcon = { IconButton(actions.onBack) { Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Voltar") } },
                actions = {
                    if (state.expense != null) {
                        IconButton(actions.onEdit) { Icon(Icons.Rounded.Edit, "Editar despesa") }
                        IconButton(actions.onDelete) { Icon(Icons.Rounded.DeleteOutline, "Excluir despesa") }
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbar) },
        bottomBar = {
            if (state.expense?.status != PaymentStatus.PAID && state.expense != null) {
                Button(
                    onClick = actions.onRequestPayment,
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                ) { Text("Marcar como paga") }
            }
        },
    ) { padding ->
        when {
            state.isLoading -> Column(Modifier.fillMaxSize().padding(padding), Arrangement.Center, Alignment.CenterHorizontally) { CircularProgressIndicator() }
            state.expense == null -> Column(Modifier.fillMaxSize().padding(padding), Arrangement.Center, Alignment.CenterHorizontally) { Text("Despesa não encontrada") }
            else -> ExpenseDetailBody(state.expense, Modifier.padding(padding))
        }
    }
    if (state.showPaymentConfirmation) PaymentConfirmationDialog(state.expense, actions.onConfirmPayment, actions.onDismissPayment)
    state.editor?.let { ExpenseEditDialog(it, state.categories, state.cards, actions) }
    if (state.showDeleteConfirmation) ExpenseDeleteDialog(state.expense, actions)
}

@Preview(name = "Pagamento pendente", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun ExpenseDetailsPreview() { MyFinancesTheme { ExpenseDetailsContent(previewExpenseDetailState(), ExpenseDetailActions()) } }

@Preview(name = "Pagamento concluído", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun PaidExpenseDetailsPreview() { MyFinancesTheme(darkTheme = true) { ExpenseDetailsContent(previewExpenseDetailState(paid = true), ExpenseDetailActions()) } }
