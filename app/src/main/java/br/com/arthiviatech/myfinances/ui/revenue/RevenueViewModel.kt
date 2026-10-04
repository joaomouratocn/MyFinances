package br.com.arthiviatech.myfinances.ui.revenue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.arthiviatech.myfinances.data.repository.RevenueRepository
import br.com.arthiviatech.myfinances.data.room.entitys.FixedRevenueEntity
import br.com.arthiviatech.myfinances.data.room.entitys.FixedRevenueVersionEntity
import br.com.arthiviatech.myfinances.data.room.entitys.RevenueEntryEntity
import br.com.arthiviatech.myfinances.core.AppClock
import br.com.arthiviatech.myfinances.domain.usecase.MaterializeFixedRevenuesUseCase
import java.math.RoundingMode
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private val RevenueDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

@OptIn(ExperimentalCoroutinesApi::class)
class RevenueViewModel(
    private val revenueRepository: RevenueRepository,
    private val materializeFixedRevenues: MaterializeFixedRevenuesUseCase,
    private val clock: AppClock,
) : ViewModel() {
    private val mutableState = MutableStateFlow(RevenueUiState())
    val uiState = mutableState.asStateFlow()

    init {
        viewModelScope.launch {
            mutableState.map { it.selectedMonth }.distinctUntilChanged().flatMapLatest { month ->
                revenueRepository.observeByMonth(month.toReferenceMonth())
            }.collect { entries ->
                update { copy(monthlyEntries = entries.map(RevenueEntryEntity::toUi)) }
            }
        }
        viewModelScope.launch {
            val selectedMonthFlow = mutableState.map { it.selectedMonth }.distinctUntilChanged()
            combine(
                selectedMonthFlow,
                revenueRepository.observeFixedRevenues(),
                selectedMonthFlow.flatMapLatest { month ->
                    revenueRepository.observeByMonth(month.toReferenceMonth())
                },
            ) { month, fixed, entries -> Triple(month, fixed, entries) }
                .collect { (month, fixedRevenues, entries) ->
                    val referenceMonth = month.toReferenceMonth()
                    val fixedEntries = entries.filter { it.fixedRevenueId != null }
                    val activeItems = fixedRevenues.mapNotNull { fixed ->
                        revenueRepository.findEffectiveVersion(fixed.id, referenceMonth)?.let { version ->
                            val occurrence = fixedEntries.firstOrNull { it.fixedRevenueId == fixed.id }
                            FixedRevenueUi(
                                id = fixed.id,
                                description = occurrence?.description ?: fixed.description,
                                amountCents = occurrence?.amountCents ?: version.defaultAmountCents,
                                expectedDay = occurrence?.let {
                                    LocalDate.ofEpochDay(it.expectedDateEpochDay).dayOfMonth
                                } ?: version.expectedDay,
                            )
                        }
                    }
                    val activeIds = activeItems.mapTo(mutableSetOf(), FixedRevenueUi::id)
                    val historicalItems = fixedEntries.mapNotNull { occurrence ->
                        val fixedId = occurrence.fixedRevenueId ?: return@mapNotNull null
                        if (fixedId in activeIds) return@mapNotNull null
                        FixedRevenueUi(
                            id = fixedId,
                            description = occurrence.description,
                            amountCents = occurrence.amountCents,
                            expectedDay = LocalDate.ofEpochDay(occurrence.expectedDateEpochDay).dayOfMonth,
                            isActive = false,
                        )
                    }
                    update { copy(fixedRevenues = activeItems + historicalItems) }
                }
        }
        viewModelScope.launch {
            mutableState.map { it.selectedMonth }.distinctUntilChanged().collect { month ->
                runCatching { materializeFixedRevenues(month.toReferenceMonth()) }
            }
        }
    }

    fun previousMonth() = update { copy(selectedMonth = selectedMonth.minusMonths(1), message = null) }
    fun nextMonth() = update { copy(selectedMonth = selectedMonth.plusMonths(1), message = null) }
    fun openTypeDialog() = update { copy(showTypeDialog = true) }
    fun dismissTypeDialog() = update { copy(showTypeDialog = false) }

    fun createFixed() = update {
        copy(
            showTypeDialog = false,
            editor = RevenueEditorUi(
                mode = RevenueEditorMode.CREATE_FIXED,
                expectedDay = selectedMonth.atDay(1).dayOfMonth.toString(),
            ),
        )
    }

    fun createEventual() = update {
        copy(
            showTypeDialog = false,
            editor = RevenueEditorUi(
                mode = RevenueEditorMode.CREATE_EVENTUAL,
                expectedDate = selectedMonth.atDay(1).format(RevenueDateFormatter),
            ),
        )
    }

    fun selectFixedRevenue(item: FixedRevenueUi) = update { copy(selectedFixedRevenue = item) }
    fun dismissFixedActions() = update { copy(selectedFixedRevenue = null) }

    fun editFuture() {
        val fixed = mutableState.value.selectedFixedRevenue ?: return
        viewModelScope.launch {
            val referenceMonth = mutableState.value.selectedMonth.toReferenceMonth()
            val version = revenueRepository.findEffectiveVersion(fixed.id, referenceMonth) ?: return@launch
            update {
                copy(
                    selectedFixedRevenue = null,
                    editor = RevenueEditorUi(
                        mode = RevenueEditorMode.EDIT_FUTURE,
                        fixedRevenueId = fixed.id,
                        versionId = version.id.takeIf { version.effectiveFromMonth == referenceMonth },
                        description = fixed.description,
                        amount = formatEditableMoney(version.defaultAmountCents),
                        expectedDay = version.expectedDay.toString(),
                    ),
                )
            }
        }
    }

    fun editCurrentMonth() {
        val fixed = mutableState.value.selectedFixedRevenue ?: return
        viewModelScope.launch {
            val month = mutableState.value.selectedMonth
            materializeFixedRevenues(month.toReferenceMonth())
            val entry = revenueRepository.findFixedEntryForMonth(fixed.id, month.toReferenceMonth()) ?: return@launch
            openEntryEditor(entry)
            update { copy(selectedFixedRevenue = null) }
        }
    }

    fun editEntry(item: RevenueEntryUi) {
        viewModelScope.launch {
            val entry = revenueRepository.findEntryById(item.id)
            if (entry != null) openEntryEditor(entry)
        }
    }

    fun requestDisableFixed() {
        val fixed = mutableState.value.selectedFixedRevenue ?: return
        update {
            copy(
                selectedFixedRevenue = null,
                disableTarget = RevenueDisableTarget.Fixed(fixed.id, fixed.description),
            )
        }
    }

    fun requestDisableEntry(item: RevenueEntryUi) = update {
        copy(disableTarget = RevenueDisableTarget.Entry(item.id, item.description))
    }

    fun dismissDisable() = update { copy(disableTarget = null) }
    fun dismissEditor() = update { copy(editor = null) }
    fun updateDescription(value: String) = updateEditor { copy(description = value, errorMessage = null) }
    fun updateAmount(value: String) = updateEditor { copy(amount = value, errorMessage = null) }
    fun updateExpectedDate(value: String) = updateEditor { copy(expectedDate = value, errorMessage = null) }
    fun updateExpectedDay(value: String) = updateEditor { copy(expectedDay = value.filter(Char::isDigit), errorMessage = null) }
    fun clearMessage() = update { copy(message = null) }

    fun saveEditor() {
        val editor = mutableState.value.editor ?: return
        val error = validate(editor)
        if (error != null) {
            updateEditor { copy(errorMessage = error) }
            return
        }
        viewModelScope.launch {
            runCatching { persist(editor) }
                .onSuccess { update { copy(editor = null, message = null) } }
                .onFailure { updateEditor { copy(errorMessage = "Não foi possível salvar a receita.") } }
        }
    }

    fun confirmDisable() {
        val target = mutableState.value.disableTarget ?: return
        viewModelScope.launch {
            when (target) {
                is RevenueDisableTarget.Fixed -> revenueRepository.disableFixedRevenue(target.id, clock.nowMillis())
                is RevenueDisableTarget.Entry -> revenueRepository.disableEntry(target.id, clock.nowMillis())
            }
            update { copy(disableTarget = null) }
        }
    }

    private suspend fun persist(editor: RevenueEditorUi) {
        val amountCents = parseRevenueMoney(editor.amount)
        val month = mutableState.value.selectedMonth
        when (editor.mode) {
            RevenueEditorMode.CREATE_FIXED -> revenueRepository.createFixedRevenue(
                FixedRevenueEntity(
                    description = editor.description.trim(),
                    startMonth = month.toReferenceMonth(),
                    categoryId = editor.categoryId,
                ),
                FixedRevenueVersionEntity(
                    fixedRevenueId = 0,
                    defaultAmountCents = amountCents,
                    expectedDay = editor.expectedDay.toInt(),
                    effectiveFromMonth = month.toReferenceMonth(),
                ),
            )
            RevenueEditorMode.CREATE_EVENTUAL -> {
                val date = LocalDate.parse(editor.expectedDate, RevenueDateFormatter)
                revenueRepository.insertEntry(
                    RevenueEntryEntity(
                        description = editor.description.trim(),
                        amountCents = amountCents,
                        expectedDateEpochDay = date.toEpochDay(),
                        referenceMonth = YearMonth.from(date).toReferenceMonth(),
                        origin = "EVENTUAL",
                        categoryId = editor.categoryId,
                    ),
                )
            }
            RevenueEditorMode.EDIT_MONTH -> {
                val existing = requireNotNull(editor.entryId?.let { revenueRepository.findEntryById(it) })
                val date = LocalDate.parse(editor.expectedDate, RevenueDateFormatter)
                revenueRepository.updateEntry(
                    existing.copy(
                        description = editor.description.trim(),
                        amountCents = amountCents,
                        expectedDateEpochDay = date.toEpochDay(),
                        referenceMonth = YearMonth.from(date).toReferenceMonth(),
                        categoryId = editor.categoryId,
                    ),
                )
            }
            RevenueEditorMode.EDIT_FUTURE -> {
                val version = FixedRevenueVersionEntity(
                    id = editor.versionId ?: 0,
                    fixedRevenueId = requireNotNull(editor.fixedRevenueId),
                    defaultAmountCents = amountCents,
                    expectedDay = editor.expectedDay.toInt(),
                    effectiveFromMonth = month.toReferenceMonth(),
                )
                if (editor.versionId == null) revenueRepository.insertVersion(version)
                else revenueRepository.updateVersion(version)
            }
        }
    }

    private fun openEntryEditor(entry: RevenueEntryEntity) {
        update {
            copy(
                editor = RevenueEditorUi(
                    mode = RevenueEditorMode.EDIT_MONTH,
                    entryId = entry.id,
                    fixedRevenueId = entry.fixedRevenueId,
                    description = entry.description,
                    amount = formatEditableMoney(entry.amountCents),
                    expectedDate = LocalDate.ofEpochDay(entry.expectedDateEpochDay).format(RevenueDateFormatter),
                    categoryId = entry.categoryId,
                ),
            )
        }
    }

    private fun validate(editor: RevenueEditorUi): String? = when {
        editor.description.isBlank() -> "Informe a descrição."
        parseRevenueMoney(editor.amount) <= 0 -> "Informe um valor maior que zero."
        editor.mode in setOf(RevenueEditorMode.CREATE_FIXED, RevenueEditorMode.EDIT_FUTURE) &&
            (editor.expectedDay.toIntOrNull() ?: 0) !in 1..31 -> "Informe um dia entre 1 e 31."
        editor.mode in setOf(RevenueEditorMode.CREATE_EVENTUAL, RevenueEditorMode.EDIT_MONTH) &&
            parseRevenueDate(editor.expectedDate) == null -> "Informe uma data válida."
        else -> null
    }

    private fun update(transform: RevenueUiState.() -> RevenueUiState) = mutableState.update(transform)
    private fun updateEditor(transform: RevenueEditorUi.() -> RevenueEditorUi) = update {
        copy(editor = editor?.transform())
    }
}

private fun RevenueEntryEntity.toUi() = RevenueEntryUi(
    id = id,
    fixedRevenueId = fixedRevenueId,
    description = description,
    amountCents = amountCents,
    expectedDateEpochDay = expectedDateEpochDay,
    categoryId = categoryId,
    isFixed = fixedRevenueId != null,
)

internal fun parseRevenueMoney(value: String): Long = runCatching {
    value.trim().replace("R$", "").replace(" ", "").replace(".", "").replace(',', '.')
        .toBigDecimal().movePointRight(2).setScale(0, RoundingMode.UNNECESSARY).longValueExact()
}.getOrDefault(0)

internal fun formatEditableMoney(cents: Long): String = "%d,%02d".format(cents / 100, cents % 100)
private fun parseRevenueDate(value: String) = runCatching { LocalDate.parse(value, RevenueDateFormatter) }.getOrNull()
private fun YearMonth.toReferenceMonth() = year * 100 + monthValue
