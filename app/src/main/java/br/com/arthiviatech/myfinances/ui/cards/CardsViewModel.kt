package br.com.arthiviatech.myfinances.ui.cards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.arthiviatech.myfinances.data.repository.CardRepository
import br.com.arthiviatech.myfinances.core.AppClock
import br.com.arthiviatech.myfinances.data.room.entitys.CardEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CardsViewModel(private val cardRepository: CardRepository, private val clock: AppClock) : ViewModel() {
    private val controls = MutableStateFlow(CardsUiState())
    val uiState = combine(cardRepository.observeAll(), controls) { cards, state ->
        state.copy(cards = cards.filter {
            if (state.filter == CardFilter.ENABLED) it.enabled else !it.enabled
        }.map(CardEntity::toUi))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CardsUiState())

    fun selectFilter(filter: CardFilter) = update { copy(filter = filter, message = null) }
    fun openCreate() = update { copy(editor = CardEditorUi(), message = null) }
    fun dismissEditor() = update { copy(editor = null) }
    fun openEdit(card: CardUi) = update {
        copy(editor = CardEditorUi(card.id, card.name, card.dueDay.toString(), card.closingDay?.toString().orEmpty()))
    }
    fun updateName(value: String) = updateEditor { copy(name = value, nameError = null) }
    fun updateDueDay(value: String) = updateEditor { copy(dueDay = value.filter(Char::isDigit).take(2), dueDayError = null) }
    fun updateClosingDay(value: String) = updateEditor { copy(closingDay = value.filter(Char::isDigit).take(2), closingDayError = null) }

    fun saveEditor() {
        val editor = controls.value.editor ?: return
        val validated = editor.validate()
        if (validated.hasErrors()) {
            update { copy(editor = validated) }
            return
        }
        viewModelScope.launch {
            val current = editor.cardId?.let { cardRepository.findById(it) }
            val card = (current ?: CardEntity(name = editor.name.trim(), dueDay = editor.dueDay.toInt())).copy(
                name = editor.name.trim(),
                dueDay = editor.dueDay.toInt(),
                closingDay = editor.closingDay.toIntOrNull(),
            )
            runCatching { if (current == null) cardRepository.insert(card) else cardRepository.update(card) }
                .onSuccess { update { copy(editor = null) } }
                .onFailure { updateEditor { copy(nameError = "Já existe um cartão com este nome.") } }
        }
    }

    fun requestStatusChange(card: CardUi) = update { copy(pendingStatusChange = card, message = null) }
    fun dismissStatusChange() = update { copy(pendingStatusChange = null) }
    fun confirmStatusChange() {
        val card = controls.value.pendingStatusChange ?: return
        viewModelScope.launch {
            val changed = if (card.enabled) cardRepository.disable(card.id, clock.nowMillis()) else cardRepository.enable(card.id)
            update { copy(pendingStatusChange = null, message = if (changed) null else "Não foi possível alterar o cartão.") }
        }
    }
    fun clearMessage() = update { copy(message = null) }
    private fun update(transform: CardsUiState.() -> CardsUiState) = controls.update(transform)
    private fun updateEditor(transform: CardEditorUi.() -> CardEditorUi) = update { copy(editor = editor?.transform()) }
}

private fun CardEditorUi.validate() = copy(
    nameError = if (name.isBlank()) "Informe o nome do cartão." else null,
    dueDayError = if ((dueDay.toIntOrNull() ?: 0) !in 1..31) "Informe um dia entre 1 e 31." else null,
    closingDayError = if (closingDay.isNotBlank() && (closingDay.toIntOrNull() ?: 0) !in 1..31) "Informe um dia entre 1 e 31 ou deixe vazio." else null,
)
private fun CardEditorUi.hasErrors() = nameError != null || dueDayError != null || closingDayError != null
private fun CardEntity.toUi() = CardUi(id, name, dueDay, closingDay, enabled)
