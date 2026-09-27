package br.com.arthiviatech.myfinances.ui.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.arthiviatech.myfinances.ui.theme.MyFinancesTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun CardsCompose(onBack: () -> Unit, modifier: Modifier = Modifier, viewModel: CardsViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CardsContent(
        uiState,
        CardsActions(
            onBack, viewModel::selectFilter, viewModel::openCreate, viewModel::openEdit,
            viewModel::requestStatusChange, viewModel::updateName, viewModel::updateDueDay,
            viewModel::updateClosingDay, viewModel::saveEditor, viewModel::dismissEditor,
            viewModel::confirmStatusChange, viewModel::dismissStatusChange, viewModel::clearMessage,
        ),
        modifier,
    )
}

data class CardsActions(
    val onBack: () -> Unit = {},
    val onFilterSelected: (CardFilter) -> Unit = {},
    val onCreate: () -> Unit = {},
    val onEdit: (CardUi) -> Unit = {},
    val onStatusChange: (CardUi) -> Unit = {},
    val onNameChange: (String) -> Unit = {},
    val onDueDayChange: (String) -> Unit = {},
    val onClosingDayChange: (String) -> Unit = {},
    val onEditorSave: () -> Unit = {},
    val onEditorDismiss: () -> Unit = {},
    val onStatusConfirm: () -> Unit = {},
    val onStatusDismiss: () -> Unit = {},
    val onMessageShown: () -> Unit = {},
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardsContent(uiState: CardsUiState, actions: CardsActions, modifier: Modifier = Modifier) {
    val snackbar = remember { SnackbarHostState() }
    LaunchedEffect(uiState.message) {
        uiState.message?.let { snackbar.showSnackbar(it); actions.onMessageShown() }
    }
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Cartões") },
                navigationIcon = {
                    IconButton(onClick = actions.onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Voltar")
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbar) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = actions.onCreate,
                icon = { Icon(Icons.Rounded.Add, contentDescription = null) },
                text = { Text("Novo cartão") },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            CardFilters(uiState.filter, actions.onFilterSelected)
            CardsList(uiState.cards, uiState.filter, actions.onEdit, actions.onStatusChange, Modifier.weight(1f))
        }
    }
    uiState.editor?.let { CardEditorDialog(it, actions) }
    uiState.pendingStatusChange?.let { CardStatusDialog(it, actions.onStatusConfirm, actions.onStatusDismiss) }
}

@Composable
private fun CardFilters(selected: CardFilter, onSelected: (CardFilter) -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(selected == CardFilter.ENABLED, { onSelected(CardFilter.ENABLED) }, { Text("Ativos") })
        FilterChip(selected == CardFilter.DISABLED, { onSelected(CardFilter.DISABLED) }, { Text("Desativados") })
    }
}

@Preview(name = "Cartões - Claro", group = "Tela completa", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun CardsPreview() { MyFinancesTheme { CardsContent(previewCardsState(), CardsActions()) } }

@Preview(name = "Cartões - Escuro", group = "Tela completa", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun CardsDarkPreview() { MyFinancesTheme(darkTheme = true) { CardsContent(previewCardsState(), CardsActions()) } }

@Preview(name = "Cartões - Paisagem", group = "Tela completa", showBackground = true, widthDp = 900, heightDp = 500)
@Composable
private fun CardsLandscapePreview() { MyFinancesTheme { CardsContent(previewCardsState(), CardsActions()) } }
