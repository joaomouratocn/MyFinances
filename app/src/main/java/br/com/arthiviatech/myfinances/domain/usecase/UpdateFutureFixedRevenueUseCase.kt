package br.com.arthiviatech.myfinances.domain.usecase

import br.com.arthiviatech.myfinances.data.repository.RevenueRepository
import br.com.arthiviatech.myfinances.data.room.entitys.FixedRevenueVersionEntity

class UpdateFutureFixedRevenueUseCase(
    private val revenueRepository: RevenueRepository,
) {
    suspend operator fun invoke(
        fixedRevenueId: Long,
        amountCents: Long,
        expectedDay: Int,
        effectiveFromMonth: Int,
    ): Long {
        require(amountCents > 0) { "O valor da receita deve ser maior que zero." }
        require(expectedDay in 1..31) { "O dia previsto deve estar entre 1 e 31." }
        val fixedRevenue = requireNotNull(revenueRepository.findFixedRevenueById(fixedRevenueId)) {
            "Receita fixa não encontrada."
        }
        require(fixedRevenue.enabled) { "A receita fixa está desativada." }
        require(effectiveFromMonth >= fixedRevenue.startMonth) {
            "A vigência não pode ser anterior ao início da receita."
        }
        effectiveFromMonth.toYearMonth()

        return revenueRepository.insertVersion(
            FixedRevenueVersionEntity(
                fixedRevenueId = fixedRevenueId,
                defaultAmountCents = amountCents,
                expectedDay = expectedDay,
                effectiveFromMonth = effectiveFromMonth,
            ),
        )
    }
}
