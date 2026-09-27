package br.com.arthiviatech.myfinances.domain.usecase

import br.com.arthiviatech.myfinances.data.repository.ExpenseRepository
import br.com.arthiviatech.myfinances.data.repository.InstallmentRepository

class DisableInstallmentsFromUseCase(
    private val installmentRepository: InstallmentRepository,
    private val expenseRepository: ExpenseRepository,
) {
    suspend operator fun invoke(
        installmentPlanId: Long,
        fromInstallmentNumber: Int,
        disabledAt: Long,
    ): Int {
        val plan = requireNotNull(installmentRepository.findById(installmentPlanId)) {
            "Parcelamento não encontrado."
        }
        require(fromInstallmentNumber in 1..plan.installmentCount) {
            "O número da parcela está fora do intervalo do parcelamento."
        }
        val disabledCount = expenseRepository.disableInstallmentsFrom(
            installmentPlanId = installmentPlanId,
            fromInstallmentNumber = fromInstallmentNumber,
            disabledAt = disabledAt,
        )
        if (disabledCount > 0) installmentRepository.disable(installmentPlanId, disabledAt)
        return disabledCount
    }
}
