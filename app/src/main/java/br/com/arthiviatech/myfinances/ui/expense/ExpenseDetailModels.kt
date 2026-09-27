package br.com.arthiviatech.myfinances.ui.expense

import java.time.LocalDate
import java.time.LocalDateTime

enum class PaymentStatus { PENDING, OVERDUE, PAID }

data class ExpenseDetailUi(
    val id: Long,
    val description: String,
    val amountCents: Long,
    val category: String,
    val purchaseDate: LocalDate,
    val dueDate: LocalDate,
    val paymentMethod: String,
    val card: String?,
    val origin: String,
    val installmentLabel: String?,
    val isInstallment: Boolean,
    val paidAt: LocalDateTime?,
    val status: PaymentStatus,
)

data class ExpenseEditUi(
    val description: String,
    val amount: String,
    val categoryId: Long,
    val purchaseDate: String,
    val dueDate: String,
    val paymentMethod: ExpensePaymentMethod,
    val cardId: Long?,
    val currentCategoryLabel: String,
    val currentCardLabel: String?,
    val errorMessage: String? = null,
)

enum class ExpenseDeleteScope { ONLY_THIS, THIS_AND_FUTURE }

data class ExpenseDetailUiState(
    val isLoading: Boolean = true,
    val expense: ExpenseDetailUi? = null,
    val showPaymentConfirmation: Boolean = false,
    val editor: ExpenseEditUi? = null,
    val showDeleteConfirmation: Boolean = false,
    val categories: List<ExpenseOptionUi> = emptyList(),
    val cards: List<ExpenseOptionUi> = emptyList(),
    val deleted: Boolean = false,
    val message: String? = null,
)
