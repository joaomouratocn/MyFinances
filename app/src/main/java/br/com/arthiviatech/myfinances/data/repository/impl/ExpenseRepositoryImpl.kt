package br.com.arthiviatech.myfinances.data.repository.impl

import br.com.arthiviatech.myfinances.data.repository.ExpenseRepository
import br.com.arthiviatech.myfinances.data.room.daos.ExpenseEntryDao
import br.com.arthiviatech.myfinances.data.room.entitys.ExpenseEntryEntity

class ExpenseRepositoryImpl(
    private val expenseDao: ExpenseEntryDao,
) : ExpenseRepository {
    override fun observeByMonth(referenceMonth: Int) = expenseDao.observeByMonth(referenceMonth)
    override fun observeByInstallmentPlan(installmentPlanId: Long) =
        expenseDao.observeByInstallmentPlan(installmentPlanId)
    override suspend fun findById(id: Long) = expenseDao.findById(id)
    override suspend fun findByRecurrenceAndMonth(recurringExpenseId: Long, referenceMonth: Int) =
        expenseDao.findByRecurrenceAndMonth(recurringExpenseId, referenceMonth)
    override suspend fun insert(expense: ExpenseEntryEntity) = expenseDao.insert(expense)
    override suspend fun insertAll(expenses: List<ExpenseEntryEntity>) = expenseDao.insertAll(expenses)
    override suspend fun update(expense: ExpenseEntryEntity) = expenseDao.update(expense)
    override suspend fun markAsPaid(id: Long, paidAt: Long) = expenseDao.markAsPaid(id, paidAt) > 0
    override suspend fun markAsPending(id: Long) = expenseDao.markAsPending(id) > 0
    override suspend fun disable(id: Long, disabledAt: Long) = expenseDao.disable(id, disabledAt) > 0
}
