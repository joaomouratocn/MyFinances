package br.com.arthiviatech.myfinances.data.repository.impl

import br.com.arthiviatech.myfinances.data.repository.MonthlySummary
import br.com.arthiviatech.myfinances.data.repository.ReportRepository
import br.com.arthiviatech.myfinances.data.room.daos.ExpenseEntryDao
import br.com.arthiviatech.myfinances.data.room.daos.RevenueEntryDao
import kotlinx.coroutines.flow.combine

class ReportRepositoryImpl(
    private val expenseDao: ExpenseEntryDao,
    private val revenueDao: RevenueEntryDao,
) : ReportRepository {
    override fun observeMonthlySummary(referenceMonth: Int) = combine(
        expenseDao.observeByMonth(referenceMonth),
        revenueDao.observeByMonth(referenceMonth),
    ) { expenses, revenues ->
        val expenseCents = expenses.sumOf { it.amountCents }
        val revenueCents = revenues.sumOf { it.amountCents }

        MonthlySummary(
            revenueCents = revenueCents,
            expenseCents = expenseCents,
            balanceCents = revenueCents - expenseCents,
            expenseByCategoryCents = expenses
                .groupBy { it.categoryId }
                .mapValues { (_, entries) -> entries.sumOf { it.amountCents } },
            expenseByCardCents = expenses
                .groupBy { it.cardId }
                .mapValues { (_, entries) -> entries.sumOf { it.amountCents } },
        )
    }
}
