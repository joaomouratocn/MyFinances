package br.com.arthiviatech.myfinances.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.arthiviatech.myfinances.ui.theme.MyFinancesTheme
import java.time.YearMonth
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeCompose(
    modifier: Modifier = Modifier,
    onNewExpense: () -> Unit = {},
    onFutureAccounts: () -> Unit = {},
    onExpenseClick: (Long) -> Unit = {},
    viewModel: HomeViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeContent(
        uiState = uiState,
        onPreviousMonth = viewModel::selectPreviousMonth,
        onNextMonth = viewModel::selectNextMonth,
        onNewExpense = onNewExpense,
        onFutureAccounts = onFutureAccounts,
        onExpenseClick = onExpenseClick,
        filterActions = HomeFilterActions(
            viewModel::updateSearch, viewModel::openFilters, viewModel::dismissFilters,
            viewModel::updateStartDate, viewModel::updateEndDate, viewModel::selectCategory,
            viewModel::selectCard, viewModel::selectStatus, viewModel::selectSort,
            viewModel::applyFilters, viewModel::clearFilters,
        ),
        modifier = modifier,
    )
}

@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    uiState: HomeUiState,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onNewExpense: () -> Unit,
    onFutureAccounts: () -> Unit,
    onExpenseClick: (Long) -> Unit,
    filterActions: HomeFilterActions = HomeFilterActions(),
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNewExpense,
                modifier = Modifier.height(48.dp),
                icon = { Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(20.dp)) },
                text = { Text("Nova despesa", style = MaterialTheme.typography.labelMedium) },
            )
        },
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
        ) {
            if (maxWidth >= 700.dp) {
                WideHomeContent(uiState, onPreviousMonth, onNextMonth, onFutureAccounts, onExpenseClick, filterActions)
            } else {
                CompactHomeContent(uiState, onPreviousMonth, onNextMonth, onFutureAccounts, onExpenseClick, filterActions)
            }
        }
    }
    if (uiState.showFilters) HomeExpenseFilterDialog(uiState, filterActions)
}

data class HomeFilterActions(
    val onSearchChange: (String) -> Unit = {}, val onOpenFilters: () -> Unit = {},
    val onDismissFilters: () -> Unit = {}, val onStartDateChange: (String) -> Unit = {},
    val onEndDateChange: (String) -> Unit = {}, val onCategorySelected: (Long?) -> Unit = {},
    val onCardSelected: (Long?) -> Unit = {}, val onStatusSelected: (ExpenseStatus?) -> Unit = {},
    val onSortSelected: (HomeExpenseSort) -> Unit = {}, val onApplyFilters: () -> Unit = {},
    val onClearFilters: () -> Unit = {},
)

@Composable
private fun CompactHomeContent(
    uiState: HomeUiState,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onFutureAccounts: () -> Unit,
    onExpenseClick: (Long) -> Unit,
    filterActions: HomeFilterActions,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        HomeHeader(uiState.selectedMonth, onPreviousMonth, onNextMonth)
        HomeBalanceCard(uiState)
        if (uiState.pendingAccountsCount > 0) {
            HomePendingAccountsCard(uiState.pendingAccountsCount, onFutureAccounts)
        }
        HomeExpenseList(uiState, onExpenseClick, filterActions)
    }
}

@Composable
private fun WideHomeContent(
    uiState: HomeUiState,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onFutureAccounts: () -> Unit,
    onExpenseClick: (Long) -> Unit,
    filterActions: HomeFilterActions,
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Column(
            modifier = Modifier
                .weight(0.9f)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            HomeHeader(uiState.selectedMonth, onPreviousMonth, onNextMonth)
            HomeBalanceCard(uiState)
            if (uiState.pendingAccountsCount > 0) {
                HomePendingAccountsCard(uiState.pendingAccountsCount, onFutureAccounts)
            }
        }
        Column(
            modifier = Modifier
                .weight(1.1f)
                .verticalScroll(rememberScrollState())
                .padding(top = 16.dp, bottom = 80.dp),
        ) {
            HomeExpenseList(uiState, onExpenseClick, filterActions)
        }
    }
}

@Preview(name = "Home - Claro", group = "Tela completa", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun HomePreview() {
    HomePreviewContent(uiState = previewHomeUiState())
}

@Preview(name = "Home - Escuro", group = "Tela completa", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun HomeDarkPreview() {
    HomePreviewContent(uiState = previewHomeUiState(), darkTheme = true)
}

@Preview(name = "Home - Paisagem", group = "Tela completa", showBackground = true, widthDp = 900, heightDp = 500)
@Composable
private fun HomeLandscapePreview() {
    HomePreviewContent(uiState = previewHomeUiState())
}

@Preview(name = "Home - Sem lançamentos", group = "Estados", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun HomeEmptyPreview() {
    HomePreviewContent(uiState = HomeUiState(selectedMonth = YearMonth.of(2026, 9)))
}

@Composable
private fun HomePreviewContent(uiState: HomeUiState, darkTheme: Boolean = false) {
    MyFinancesTheme(darkTheme = darkTheme) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            HomeContent(
                uiState = uiState,
                onPreviousMonth = {},
                onNextMonth = {},
                onNewExpense = {},
                onFutureAccounts = {},
                onExpenseClick = {},
            )
        }
    }
}
