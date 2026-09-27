package br.com.arthiviatech.myfinances.ui.future

import java.time.YearMonth

data class FutureAccountUi(
    val id: Long,
    val description: String,
    val category: String,
    val dueDay: Int,
    val paymentMethod: String,
    val card: String?,
    val paused: Boolean,
)

data class FutureValueEditorUi(
    val account: FutureAccountUi,
    val amount: String = "",
    val dueDate: String,
    val errorMessage: String? = null,
)

data class FutureAccountEditorUi(
    val account: FutureAccountUi,
    val description: String = account.description,
    val dueDay: String = account.dueDay.toString(),
    val errorMessage: String? = null,
)

enum class FutureAccountAction { PAUSE, RESUME, END }

data class FutureActionConfirmationUi(
    val account: FutureAccountUi,
    val action: FutureAccountAction,
)

data class FutureUiState(
    val selectedMonth: YearMonth = YearMonth.now(),
    val pending: List<FutureAccountUi> = emptyList(),
    val paused: List<FutureAccountUi> = emptyList(),
    val editor: FutureValueEditorUi? = null,
    val accountEditor: FutureAccountEditorUi? = null,
    val selectedAccount: FutureAccountUi? = null,
    val confirmation: FutureActionConfirmationUi? = null,
    val message: String? = null,
)
