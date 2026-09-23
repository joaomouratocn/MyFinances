package br.com.arthiviatech.myfinances.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
        modifier = modifier,
    )
}

@Composable
fun HomeContent(
    uiState: HomeUiState,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onNewExpense: () -> Unit,
    onFutureAccounts: () -> Unit,
    onExpenseClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNewExpense,
                icon = { Icon(Icons.Rounded.Add, contentDescription = null) },
                text = { Text("Nova despesa") },
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
                WideHomeContent(uiState, onPreviousMonth, onNextMonth, onFutureAccounts, onExpenseClick)
            } else {
                CompactHomeContent(uiState, onPreviousMonth, onNextMonth, onFutureAccounts, onExpenseClick)
            }
        }
    }
}

@Composable
private fun CompactHomeContent(
    uiState: HomeUiState,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onFutureAccounts: () -> Unit,
    onExpenseClick: (Long) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        HomeHeader(uiState.selectedMonth, onPreviousMonth, onNextMonth)
        HomeBalanceCard(uiState)
        if (uiState.pendingAccountsCount > 0) {
            HomePendingAccountsCard(uiState.pendingAccountsCount, onFutureAccounts)
        }
        HomeExpenseList(uiState.expenses, onExpenseClick)
    }
}

@Composable
private fun WideHomeContent(
    uiState: HomeUiState,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onFutureAccounts: () -> Unit,
    onExpenseClick: (Long) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Column(
            modifier = Modifier
                .weight(0.9f)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 96.dp),
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
                .padding(top = 16.dp, bottom = 96.dp),
        ) {
            HomeExpenseList(uiState.expenses, onExpenseClick)
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
