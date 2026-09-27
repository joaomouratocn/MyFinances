package br.com.arthiviatech.myfinances.ui.invoices

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.arthiviatech.myfinances.ui.theme.MyFinancesTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun InvoicesCompose(
    onBack: () -> Unit,
    onExpenseClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: InvoicesViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    InvoicesContent(state, onBack, viewModel::previousMonth, viewModel::nextMonth, onExpenseClick, modifier)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoicesContent(
    state: InvoicesUiState,
    onBack: () -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onExpenseClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Faturas") },
                navigationIcon = { IconButton(onBack) { Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Voltar") } },
            )
        },
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            InvoicesMonthSelector(state.selectedMonth, onPreviousMonth, onNextMonth)
            InvoicesList(state, onExpenseClick, Modifier.weight(1f))
        }
    }
}

@Preview(name = "Faturas - Claro", group = "Tela completa", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun InvoicesPreview() { MyFinancesTheme { InvoicesContent(previewInvoicesState(), {}, {}, {}, {}) } }

@Preview(name = "Faturas - Escuro", group = "Tela completa", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun InvoicesDarkPreview() { MyFinancesTheme(darkTheme = true) { InvoicesContent(previewInvoicesState(), {}, {}, {}, {}) } }

@Preview(name = "Faturas - Paisagem", group = "Tela completa", showBackground = true, widthDp = 900, heightDp = 500)
@Composable
private fun InvoicesLandscapePreview() { MyFinancesTheme { InvoicesContent(previewInvoicesState(), {}, {}, {}, {}) } }
