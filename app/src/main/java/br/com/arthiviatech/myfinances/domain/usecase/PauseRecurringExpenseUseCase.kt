package br.com.arthiviatech.myfinances.domain.usecase

import br.com.arthiviatech.myfinances.data.repository.RecurringExpenseRepository

class PauseRecurringExpenseUseCase(
    private val recurringExpenseRepository: RecurringExpenseRepository,
) {
    suspend operator fun invoke(recurringExpenseId: Long, pausedAt: Long): Long {
        val recurringExpense = requireNotNull(recurringExpenseRepository.findById(recurringExpenseId)) {
            "Conta recorrente não encontrada."
        }
        require(recurringExpense.enabled) { "A conta recorrente está desativada." }
        check(recurringExpenseRepository.findOpenPause(recurringExpenseId) == null) {
            "A conta recorrente já está pausada."
        }
        return recurringExpenseRepository.pause(recurringExpenseId, pausedAt)
    }
}
