package br.com.arthiviatech.myfinances.ui.reports

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.arthiviatech.myfinances.ui.theme.MyFinancesTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun ReportsCompose(modifier: Modifier = Modifier, viewModel: ReportsViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ReportsContent(uiState, viewModel::previousMonth, viewModel::nextMonth, modifier)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsContent(
    uiState: ReportsUiState,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text("Relatórios") }) },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item { ReportsMonthSelector(uiState.selectedMonth, onPreviousMonth, onNextMonth) }
            item { ReportsSummary(uiState) }
            item { ReportBreakdownSection("Despesas por categoria", uiState.categories) }
            item { ReportBreakdownSection("Despesas por cartão", uiState.cards) }
        }
    }
}

@Preview(name = "Relatórios - Claro", group = "Tela completa", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun ReportsPreview() { MyFinancesTheme { ReportsContent(previewReportsState(), {}, {}) } }

@Preview(name = "Relatórios - Escuro", group = "Tela completa", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun ReportsDarkPreview() { MyFinancesTheme(darkTheme = true) { ReportsContent(previewReportsState(), {}, {}) } }

@Preview(name = "Relatórios - Paisagem", group = "Tela completa", showBackground = true, widthDp = 900, heightDp = 500)
@Composable
private fun ReportsLandscapePreview() { MyFinancesTheme { ReportsContent(previewReportsState(), {}, {}) } }
