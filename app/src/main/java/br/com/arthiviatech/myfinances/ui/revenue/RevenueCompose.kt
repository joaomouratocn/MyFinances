package br.com.arthiviatech.myfinances.ui.revenue

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.arthiviatech.myfinances.ui.theme.MyFinancesTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun RevenueCompose(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RevenueViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    RevenueContent(
        uiState = uiState,
        actions = RevenueActions(
            onBack = onBack,
            onPreviousMonth = viewModel::previousMonth,
            onNextMonth = viewModel::nextMonth,
            onNewRevenue = viewModel::openTypeDialog,
            onTypeDismiss = viewModel::dismissTypeDialog,
            onCreateFixed = viewModel::createFixed,
            onCreateEventual = viewModel::createEventual,
            onFixedClick = viewModel::selectFixedRevenue,
            onEntryEdit = viewModel::editEntry,
            onEntryDisable = viewModel::requestDisableEntry,
            onFixedActionsDismiss = viewModel::dismissFixedActions,
            onEditCurrentMonth = viewModel::editCurrentMonth,
            onEditFuture = viewModel::editFuture,
            onFixedDisable = viewModel::requestDisableFixed,
            onEditorDismiss = viewModel::dismissEditor,
            onDescriptionChange = viewModel::updateDescription,
            onAmountChange = viewModel::updateAmount,
            onExpectedDateChange = viewModel::updateExpectedDate,
            onExpectedDayChange = viewModel::updateExpectedDay,
            onEditorSave = viewModel::saveEditor,
            onDisableDismiss = viewModel::dismissDisable,
            onDisableConfirm = viewModel::confirmDisable,
            onMessageShown = viewModel::clearMessage,
        ),
        modifier = modifier,
    )
}

data class RevenueActions(
    val onBack: () -> Unit = {},
    val onPreviousMonth: () -> Unit = {},
    val onNextMonth: () -> Unit = {},
    val onNewRevenue: () -> Unit = {},
    val onTypeDismiss: () -> Unit = {},
    val onCreateFixed: () -> Unit = {},
    val onCreateEventual: () -> Unit = {},
    val onFixedClick: (FixedRevenueUi) -> Unit = {},
    val onEntryEdit: (RevenueEntryUi) -> Unit = {},
    val onEntryDisable: (RevenueEntryUi) -> Unit = {},
    val onFixedActionsDismiss: () -> Unit = {},
    val onEditCurrentMonth: () -> Unit = {},
    val onEditFuture: () -> Unit = {},
    val onFixedDisable: () -> Unit = {},
    val onEditorDismiss: () -> Unit = {},
    val onDescriptionChange: (String) -> Unit = {},
    val onAmountChange: (String) -> Unit = {},
    val onExpectedDateChange: (String) -> Unit = {},
    val onExpectedDayChange: (String) -> Unit = {},
    val onEditorSave: () -> Unit = {},
    val onDisableDismiss: () -> Unit = {},
    val onDisableConfirm: () -> Unit = {},
    val onMessageShown: () -> Unit = {},
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RevenueContent(
    uiState: RevenueUiState,
    actions: RevenueActions,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            snackbarHostState.showSnackbar(it)
            actions.onMessageShown()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Receitas") },
                navigationIcon = {
                    IconButton(onClick = actions.onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Voltar")
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = actions.onNewRevenue,
                icon = { Icon(Icons.Rounded.Add, contentDescription = null) },
                text = { Text("Nova receita") },
            )
        },
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            RevenueMonthSelector(uiState.selectedMonth, actions.onPreviousMonth, actions.onNextMonth)
            RevenueList(
                fixedRevenues = uiState.fixedRevenues,
                monthlyEntries = uiState.monthlyEntries,
                onFixedClick = actions.onFixedClick,
                onEntryEdit = actions.onEntryEdit,
                onEntryDisable = actions.onEntryDisable,
                modifier = Modifier.weight(1f),
            )
        }
    }

    if (uiState.showTypeDialog) {
        RevenueTypeDialog(actions.onCreateFixed, actions.onCreateEventual, actions.onTypeDismiss)
    }
    uiState.editor?.let { editor ->
        RevenueEditorDialog(editor, actions)
    }
    uiState.selectedFixedRevenue?.let { fixed ->
        FixedRevenueActionsDialog(fixed, actions)
    }
    uiState.disableTarget?.let { target ->
        RevenueDisableDialog(target, actions.onDisableConfirm, actions.onDisableDismiss)
    }
}

@Preview(name = "Receitas - Claro", group = "Tela completa", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun RevenuePreview() {
    MyFinancesTheme { RevenueContent(previewRevenueUiState(), RevenueActions()) }
}

@Preview(name = "Receitas - Escuro", group = "Tela completa", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun RevenueDarkPreview() {
    MyFinancesTheme(darkTheme = true) { RevenueContent(previewRevenueUiState(), RevenueActions()) }
}

@Preview(name = "Receitas - Paisagem", group = "Tela completa", showBackground = true, widthDp = 900, heightDp = 500)
@Composable
private fun RevenueLandscapePreview() {
    MyFinancesTheme { RevenueContent(previewRevenueUiState(), RevenueActions()) }
}
