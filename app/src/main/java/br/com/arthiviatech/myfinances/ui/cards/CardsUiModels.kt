package br.com.arthiviatech.myfinances.ui.cards

enum class CardFilter { ENABLED, DISABLED }

data class CardUi(val id: Long, val name: String, val dueDay: Int, val closingDay: Int?, val enabled: Boolean)

data class CardEditorUi(
    val cardId: Long? = null,
    val name: String = "",
    val dueDay: String = "",
    val closingDay: String = "",
    val nameError: String? = null,
    val dueDayError: String? = null,
    val closingDayError: String? = null,
)

data class CardsUiState(
    val cards: List<CardUi> = emptyList(),
    val filter: CardFilter = CardFilter.ENABLED,
    val editor: CardEditorUi? = null,
    val pendingStatusChange: CardUi? = null,
    val message: String? = null,
)
