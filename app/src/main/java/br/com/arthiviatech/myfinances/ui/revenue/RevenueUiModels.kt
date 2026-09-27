package br.com.arthiviatech.myfinances.ui.revenue

import java.time.YearMonth

data class RevenueOptionUi(val id: Long, val label: String)

data class FixedRevenueUi(
    val id: Long,
    val description: String,
    val amountCents: Long,
    val expectedDay: Int,
    val isActive: Boolean = true,
)

data class RevenueEntryUi(
    val id: Long,
    val fixedRevenueId: Long?,
    val description: String,
    val amountCents: Long,
    val expectedDateEpochDay: Long,
    val categoryId: Long?,
    val isFixed: Boolean,
)

enum class RevenueEditorMode {
    CREATE_FIXED,
    CREATE_EVENTUAL,
    EDIT_MONTH,
    EDIT_FUTURE,
}

data class RevenueEditorUi(
    val mode: RevenueEditorMode,
    val entryId: Long? = null,
    val fixedRevenueId: Long? = null,
    val versionId: Long? = null,
    val description: String = "",
    val amount: String = "",
    val expectedDate: String = "",
    val expectedDay: String = "",
    val categoryId: Long? = null,
    val errorMessage: String? = null,
)

sealed interface RevenueDisableTarget {
    val id: Long
    val description: String

    data class Fixed(override val id: Long, override val description: String) : RevenueDisableTarget
    data class Entry(override val id: Long, override val description: String) : RevenueDisableTarget
}

data class RevenueUiState(
    val selectedMonth: YearMonth = YearMonth.now(),
    val fixedRevenues: List<FixedRevenueUi> = emptyList(),
    val monthlyEntries: List<RevenueEntryUi> = emptyList(),
    val categories: List<RevenueOptionUi> = emptyList(),
    val showTypeDialog: Boolean = false,
    val editor: RevenueEditorUi? = null,
    val selectedFixedRevenue: FixedRevenueUi? = null,
    val disableTarget: RevenueDisableTarget? = null,
    val message: String? = null,
)
