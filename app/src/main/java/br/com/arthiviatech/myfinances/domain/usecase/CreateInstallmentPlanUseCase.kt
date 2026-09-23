package br.com.arthiviatech.myfinances.domain.usecase

import br.com.arthiviatech.myfinances.data.repository.InstallmentRepository
import br.com.arthiviatech.myfinances.data.room.entitys.ExpenseEntryEntity
import br.com.arthiviatech.myfinances.data.room.entitys.InstallmentPlanEntity
import java.time.LocalDate
import java.time.YearMonth

class CreateInstallmentPlanUseCase(
    private val installmentRepository: InstallmentRepository,
    private val calculateInstallmentDates: CalculateInstallmentDatesUseCase,
) {
    suspend operator fun invoke(
        plan: InstallmentPlanEntity,
        purchaseDate: LocalDate,
    ): Long {
        require(plan.installmentCount > 0) { "A quantidade de parcelas deve ser maior que zero." }
        require(plan.installmentAmountCents > 0) { "O valor da parcela deve ser maior que zero." }
        require(plan.informationalTotalCents == plan.installmentAmountCents * plan.installmentCount) {
            "O total informativo não corresponde às parcelas."
        }
        val firstDueDate = LocalDate.ofEpochDay(plan.firstDueDateEpochDay)
        val dueDates = calculateInstallmentDates(firstDueDate, plan.installmentCount)
        val installments = dueDates.mapIndexed { index, dueDate ->
            ExpenseEntryEntity(
                description = plan.description,
                amountCents = plan.installmentAmountCents,
                categoryId = plan.categoryId,
                purchaseDateEpochDay = purchaseDate.toEpochDay(),
                dueDateEpochDay = dueDate.toEpochDay(),
                referenceMonth = YearMonth.from(dueDate).toReferenceMonth(),
                paymentMethod = plan.paymentMethod,
                cardId = plan.cardId,
                origin = ORIGIN_INSTALLMENT,
                installmentNumber = index + 1,
                installmentCount = plan.installmentCount,
            )
        }
        return installmentRepository.create(plan, installments)
    }

    private companion object {
        const val ORIGIN_INSTALLMENT = "INSTALLMENT"
    }
}
