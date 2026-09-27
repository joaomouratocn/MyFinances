package br.com.arthiviatech.myfinances.ui.cards

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import br.com.arthiviatech.myfinances.ui.theme.MyFinancesTheme

@Composable
fun CardStatusDialog(card: CardUi, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    val action = if (card.enabled) "Desativar" else "Reativar"
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("$action ${card.name}?") },
        text = { Text(if (card.enabled) "O cartão deixará de aparecer em novos lançamentos. As despesas anteriores serão preservadas." else "O cartão voltará a ficar disponível para novos lançamentos.") },
        confirmButton = { TextButton(onConfirm) { Text(action) } },
        dismissButton = { TextButton(onDismiss) { Text("Cancelar") } },
    )
}

@Preview(name = "Desativar cartão", showBackground = true)
@Composable
private fun CardStatusDialogPreview() { MyFinancesTheme { CardStatusDialog(previewCards().first(), {}, {}) } }
