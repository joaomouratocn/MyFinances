package br.com.arthiviatech.myfinances.ui.future

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.arthiviatech.myfinances.ui.theme.MyFinancesTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun FutureCompose(modifier: Modifier = Modifier, viewModel: FutureViewModel = koinViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    FutureContent(
        state,
        FutureActions(
            viewModel::previousMonth, viewModel::nextMonth, viewModel::openValueEditor,
            viewModel::selectAccount, viewModel::dismissActions, viewModel::updateAmount,
            viewModel::updateDueDate, viewModel::confirmValue, viewModel::dismissEditor,
            { viewModel.requestAction(FutureAccountAction.PAUSE) },
            { viewModel.requestAction(FutureAccountAction.RESUME) },
            { viewModel.requestAction(FutureAccountAction.END) },
            viewModel::confirmAction, viewModel::dismissConfirmation, viewModel::clearMessage,
            viewModel::openAccountEditor, viewModel::dismissAccountEditor,
            viewModel::updateAccountDescription, viewModel::updateAccountDueDay, viewModel::saveAccountEditor,
        ),
        modifier,
    )
}

data class FutureActions(
    val onPreviousMonth: () -> Unit = {}, val onNextMonth: () -> Unit = {},
    val onEnterValue: (FutureAccountUi) -> Unit = {}, val onAccountClick: (FutureAccountUi) -> Unit = {},
    val onActionsDismiss: () -> Unit = {}, val onAmountChange: (String) -> Unit = {},
    val onDueDateChange: (String) -> Unit = {}, val onValueConfirm: () -> Unit = {},
    val onEditorDismiss: () -> Unit = {}, val onPause: () -> Unit = {}, val onResume: () -> Unit = {},
    val onEnd: () -> Unit = {}, val onActionConfirm: () -> Unit = {}, val onConfirmationDismiss: () -> Unit = {},
    val onMessageShown: () -> Unit = {},
    val onEditAccount: () -> Unit = {}, val onAccountEditorDismiss: () -> Unit = {},
    val onAccountDescriptionChange: (String) -> Unit = {}, val onAccountDueDayChange: (String) -> Unit = {},
    val onAccountEditorSave: () -> Unit = {},
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FutureContent(state: FutureUiState, actions: FutureActions, modifier: Modifier = Modifier) {
    val snackbar = remember { SnackbarHostState() }
    LaunchedEffect(state.message) { state.message?.let { snackbar.showSnackbar(it); actions.onMessageShown() } }
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text("Contas futuras") }) },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            FutureMonthSelector(state.selectedMonth, actions.onPreviousMonth, actions.onNextMonth)
            FutureAccountsList(state.pending, state.paused, actions.onEnterValue, actions.onAccountClick, Modifier.weight(1f))
        }
    }
    state.editor?.let { FutureValueDialog(it, actions) }
    state.selectedAccount?.let { FutureActionsDialog(it, actions) }
    state.confirmation?.let { FutureConfirmationDialog(it, actions.onActionConfirm, actions.onConfirmationDismiss) }
    state.accountEditor?.let { FutureAccountEditDialog(it, actions) }
}

@Preview(name = "Contas futuras - Claro", group = "Tela completa", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun FuturePreview() { MyFinancesTheme { FutureContent(previewFutureState(), FutureActions()) } }

@Preview(name = "Contas futuras - Escuro", group = "Tela completa", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun FutureDarkPreview() { MyFinancesTheme(darkTheme = true) { FutureContent(previewFutureState(), FutureActions()) } }

@Preview(name = "Contas futuras - Paisagem", group = "Tela completa", showBackground = true, widthDp = 900, heightDp = 500)
@Composable
private fun FutureLandscapePreview() { MyFinancesTheme { FutureContent(previewFutureState(), FutureActions()) } }
