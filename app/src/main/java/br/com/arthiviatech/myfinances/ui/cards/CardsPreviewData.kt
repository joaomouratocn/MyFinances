package br.com.arthiviatech.myfinances.ui.cards

internal fun previewCards() = listOf(
    CardUi(1, "Cartão principal", 10, 3, true),
    CardUi(2, "Compras", 22, null, true),
)
internal fun previewCardsState() = CardsUiState(cards = previewCards())
