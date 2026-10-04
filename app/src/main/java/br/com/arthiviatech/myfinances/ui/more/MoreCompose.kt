package br.com.arthiviatech.myfinances.ui.more

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.arthiviatech.myfinances.ui.theme.MyFinancesTheme
import br.com.arthiviatech.myfinances.ui.tour.tourTarget

@Composable
fun MoreCompose(
    onRevenues: () -> Unit = {},
    onCards: () -> Unit = {},
    onInvoices: () -> Unit = {},
    onCategories: () -> Unit = {},
    appLockEnabled: Boolean = true,
    onAppLockEnabledChange: (Boolean) -> Unit = {},
) {
    MoreContent(onRevenues, onCards, onInvoices, onCategories, appLockEnabled, onAppLockEnabledChange)
}

@Composable
fun MoreContent(
    onRevenues: () -> Unit,
    onCards: () -> Unit,
    onInvoices: () -> Unit,
    onCategories: () -> Unit,
    appLockEnabled: Boolean = true,
    onAppLockEnabledChange: (Boolean) -> Unit = {},
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        if (maxWidth >= 700.dp) {
            MoreWideContent(onRevenues, onCards, onInvoices, onCategories, appLockEnabled, onAppLockEnabledChange)
        } else {
            MoreCompactContent(onRevenues, onCards, onInvoices, onCategories, appLockEnabled, onAppLockEnabledChange)
        }
    }
}

@Composable
private fun MoreCompactContent(
    onRevenues: () -> Unit,
    onCards: () -> Unit,
    onInvoices: () -> Unit,
    onCategories: () -> Unit,
    appLockEnabled: Boolean,
    onAppLockEnabledChange: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        MoreTitle()
        MoreManagementSection(onRevenues, onCards, onInvoices, onCategories)
        Box(Modifier.tourTarget("more_security")) {
            MoreSecuritySection(appLockEnabled, onAppLockEnabledChange)
        }
    }
}

@Composable
private fun MoreWideContent(
    onRevenues: () -> Unit,
    onCards: () -> Unit,
    onInvoices: () -> Unit,
    onCategories: () -> Unit,
    appLockEnabled: Boolean,
    onAppLockEnabledChange: (Boolean) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        MoreTitle()
        Row(
            modifier = Modifier.fillMaxSize().padding(top = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Column(Modifier.weight(1f)) {
                MoreManagementSection(onRevenues, onCards, onInvoices, onCategories)
            }
            Column(
                modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                Box(Modifier.tourTarget("more_security")) {
                    MoreSecuritySection(appLockEnabled, onAppLockEnabledChange)
                }
            }
        }
    }
}

@Composable
private fun MoreTitle() {
    Column {
        Text("Mais", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(
            "Cadastros e informações do aplicativo",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview(name = "Mais - Claro", group = "Tela completa", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun MorePreview() {
    MyFinancesTheme { MoreContent({}, {}, {}, {}) }
}

@Preview(name = "Mais - Escuro", group = "Tela completa", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun MoreDarkPreview() {
    MyFinancesTheme(darkTheme = true) { MoreContent({}, {}, {}, {}) }
}

@Preview(name = "Mais - Paisagem", group = "Tela completa", showBackground = true, widthDp = 900, heightDp = 500)
@Composable
private fun MoreLandscapePreview() {
    MyFinancesTheme { MoreContent({}, {}, {}, {}) }
}
