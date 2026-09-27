package br.com.arthiviatech.myfinances.data.repository

import kotlinx.coroutines.flow.Flow

data class MonthlySummary(
    val revenueCents: Long,
    val expenseCents: Long,
    val balanceCents: Long,
    val expenseByCategoryCents: Map<Long, Long>,
    val expenseByCardCents: Map<Long?, Long>,
)

interface ReportRepository {
    fun observeMonthlySummary(referenceMonth: Int): Flow<MonthlySummary>
}
