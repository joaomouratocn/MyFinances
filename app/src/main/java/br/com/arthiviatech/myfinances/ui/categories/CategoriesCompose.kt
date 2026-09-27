package br.com.arthiviatech.myfinances.ui.categories

import androidx.compose.foundation.layout.Arrangement
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
fun CategoriesCompose(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CategoriesViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CategoriesContent(
        uiState = uiState,
        actions = CategoriesActions(
            onBack = onBack,
            onFilterSelected = viewModel::selectFilter,
            onCreate = viewModel::openCreate,
            onEdit = viewModel::openEdit,
            onStatusChange = viewModel::requestStatusChange,
            onEditorNameChange = viewModel::updateEditorName,
            onEditorSave = viewModel::saveEditor,
            onEditorDismiss = viewModel::dismissEditor,
            onStatusChangeConfirm = viewModel::confirmStatusChange,
            onStatusChangeDismiss = viewModel::dismissStatusChange,
            onMessageShown = viewModel::clearMessage,
        ),
        modifier = modifier,
    )
}

data class CategoriesActions(
    val onBack: () -> Unit = {},
    val onFilterSelected: (CategoryFilter) -> Unit = {},
    val onCreate: () -> Unit = {},
    val onEdit: (CategoryUi) -> Unit = {},
    val onStatusChange: (CategoryUi) -> Unit = {},
    val onEditorNameChange: (String) -> Unit = {},
    val onEditorSave: () -> Unit = {},
    val onEditorDismiss: () -> Unit = {},
    val onStatusChangeConfirm: () -> Unit = {},
    val onStatusChangeDismiss: () -> Unit = {},
    val onMessageShown: () -> Unit = {},
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesContent(
    uiState: CategoriesUiState,
    actions: CategoriesActions,
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
                title = { Text("Categorias") },
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
                onClick = actions.onCreate,
                icon = { Icon(Icons.Rounded.Add, contentDescription = null) },
                text = { Text("Nova categoria") },
            )
        },
    ) { innerPadding ->
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            CategoryFilters(
                selected = uiState.filter,
                onSelected = actions.onFilterSelected,
            )
            CategoryList(
                categories = uiState.categories,
                filter = uiState.filter,
                onEdit = actions.onEdit,
                onStatusChange = actions.onStatusChange,
                modifier = Modifier.weight(1f),
            )
        }
    }

    uiState.editor?.let { editor ->
        CategoryEditorDialog(
            editor = editor,
            onNameChange = actions.onEditorNameChange,
            onSave = actions.onEditorSave,
            onDismiss = actions.onEditorDismiss,
        )
    }
    uiState.pendingStatusChange?.let { category ->
        CategoryStatusDialog(
            category = category,
            onConfirm = actions.onStatusChangeConfirm,
            onDismiss = actions.onStatusChangeDismiss,
        )
    }
}

@Composable
private fun CategoryFilters(selected: CategoryFilter, onSelected: (CategoryFilter) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FilterChip(
            selected = selected == CategoryFilter.ENABLED,
            onClick = { onSelected(CategoryFilter.ENABLED) },
            label = { Text("Ativas") },
        )
        FilterChip(
            selected = selected == CategoryFilter.DISABLED,
            onClick = { onSelected(CategoryFilter.DISABLED) },
            label = { Text("Desativadas") },
        )
    }
}

@Preview(name = "Categorias - Ativas", group = "Tela completa", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun CategoriesPreview() {
    MyFinancesTheme {
        CategoriesContent(previewCategoriesUiState(), CategoriesActions())
    }
}

@Preview(name = "Categorias - Escuro", group = "Tela completa", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun CategoriesDarkPreview() {
    MyFinancesTheme(darkTheme = true) {
        CategoriesContent(previewCategoriesUiState(), CategoriesActions())
    }
}

@Preview(name = "Categorias - Paisagem", group = "Tela completa", showBackground = true, widthDp = 900, heightDp = 500)
@Composable
private fun CategoriesLandscapePreview() {
    MyFinancesTheme {
        CategoriesContent(previewCategoriesUiState(), CategoriesActions())
    }
}
