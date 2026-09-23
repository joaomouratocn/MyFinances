package br.com.arthiviatech.myfinances.domain.usecase

import br.com.arthiviatech.myfinances.data.repository.ExpenseRepository
import br.com.arthiviatech.myfinances.data.repository.RecurringExpenseRepository
import br.com.arthiviatech.myfinances.data.room.entitys.ExpenseEntryEntity
import java.time.LocalDate

class ConfirmRecurringExpenseForMonthUseCase(
    private val recurringExpenseRepository: RecurringExpenseRepository,
    private val expenseRepository: ExpenseRepository,
    private val resolveDateForMonth: ResolveDateForMonthUseCase,
) {
    suspend operator fun invoke(
        recurringExpenseId: Long,
        referenceMonth: Int,
        amountCents: Long,
        purchaseDate: LocalDate,
    ): Long {
        require(amountCents > 0) { "O valor da despesa deve ser maior que zero." }
        val recurringExpense = requireNotNull(recurringExpenseRepository.findById(recurringExpenseId)) {
            "Conta recorrente não encontrada."
        }
        require(recurringExpense.enabled) { "A conta recorrente está desativada." }
        require(recurringExpense.startMonth <= referenceMonth) { "O mês é anterior ao início da recorrência." }
        require(recurringExpenseRepository.findOpenPause(recurringExpenseId) == null) {
            "A conta recorrente está pausada."
        }
        check(expenseRepository.findByRecurrenceAndMonth(recurringExpenseId, referenceMonth) == null) {
            "Já existe um lançamento desta recorrência no mês informado."
        }
        val dueDate = resolveDateForMonth(referenceMonth.toYearMonth(), recurringExpense.dueDay)

        return recurringExpenseRepository.confirmForMonth(
            ExpenseEntryEntity(
                description = recurringExpense.description,
                amountCents = amountCents,
                categoryId = recurringExpense.categoryId,
                purchaseDateEpochDay = purchaseDate.toEpochDay(),
                dueDateEpochDay = dueDate.toEpochDay(),
                referenceMonth = referenceMonth,
                paymentMethod = recurringExpense.paymentMethod,
                cardId = recurringExpense.cardId,
                origin = ORIGIN_RECURRING,
                recurringExpenseId = recurringExpenseId,
            ),
        )
    }

    private companion object {
        const val ORIGIN_RECURRING = "RECURRING"
    }
}
