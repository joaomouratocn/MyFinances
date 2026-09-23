package br.com.arthiviatech.myfinances.domain.usecase

import br.com.arthiviatech.myfinances.data.repository.RevenueRepository
import br.com.arthiviatech.myfinances.data.room.entitys.RevenueEntryEntity

class MaterializeFixedRevenuesUseCase(
    private val revenueRepository: RevenueRepository,
    private val resolveDateForMonth: ResolveDateForMonthUseCase,
) {
    suspend operator fun invoke(referenceMonth: Int): Int {
        val month = referenceMonth.toYearMonth()
        val entries = revenueRepository.findFixedRevenuesEligibleForMonth(referenceMonth).mapNotNull { fixed ->
            if (revenueRepository.findFixedEntryForMonth(fixed.id, referenceMonth) != null) {
                return@mapNotNull null
            }
            val version = revenueRepository.findEffectiveVersion(fixed.id, referenceMonth)
                ?: return@mapNotNull null
            val expectedDate = resolveDateForMonth(month, version.expectedDay)

            RevenueEntryEntity(
                description = fixed.description,
                amountCents = version.defaultAmountCents,
                expectedDateEpochDay = expectedDate.toEpochDay(),
                referenceMonth = referenceMonth,
                origin = ORIGIN_FIXED,
                fixedRevenueId = fixed.id,
                categoryId = fixed.categoryId,
            )
        }
        if (entries.isNotEmpty()) revenueRepository.insertEntries(entries)
        return entries.size
    }

    private companion object {
        const val ORIGIN_FIXED = "FIXED"
    }
}
