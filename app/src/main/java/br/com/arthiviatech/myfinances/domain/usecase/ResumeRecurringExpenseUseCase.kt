package br.com.arthiviatech.myfinances.domain.usecase

import br.com.arthiviatech.myfinances.data.repository.RecurringExpenseRepository

class ResumeRecurringExpenseUseCase(
    private val recurringExpenseRepository: RecurringExpenseRepository,
) {
    suspend operator fun invoke(
        recurringExpenseId: Long,
        resumedAt: Long,
        resumesFromMonth: Int,
    ) {
        require(recurringExpenseRepository.resume(recurringExpenseId, resumedAt, resumesFromMonth)) {
            "A conta recorrente não possui uma pausa aberta."
        }
    }
}
