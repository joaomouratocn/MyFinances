package br.com.arthiviatech.myfinances.data.repository.impl

import androidx.room.withTransaction
import br.com.arthiviatech.myfinances.data.repository.RecurringExpenseRepository
import br.com.arthiviatech.myfinances.data.room.AppDatabase
import br.com.arthiviatech.myfinances.data.room.daos.ExpenseEntryDao
import br.com.arthiviatech.myfinances.data.room.daos.RecurrencePauseDao
import br.com.arthiviatech.myfinances.data.room.daos.RecurringExpenseDao
import br.com.arthiviatech.myfinances.data.room.entitys.ExpenseEntryEntity
import br.com.arthiviatech.myfinances.data.room.entitys.RecurrencePauseEntity
import br.com.arthiviatech.myfinances.data.room.entitys.RecurringExpenseEntity

class RecurringExpenseRepositoryImpl(
    private val database: AppDatabase,
    private val recurringExpenseDao: RecurringExpenseDao,
    private val recurrencePauseDao: RecurrencePauseDao,
    private val expenseDao: ExpenseEntryDao,
) : RecurringExpenseRepository {
    override fun observeEnabled() = recurringExpenseDao.observeEnabled()
    override fun observeAll() = recurringExpenseDao.observeAll()
    override fun observePauses(recurringExpenseId: Long) =
        recurrencePauseDao.observeByRecurringExpense(recurringExpenseId)
    override suspend fun findById(id: Long) = recurringExpenseDao.findById(id)
    override suspend fun findOpenPause(recurringExpenseId: Long) =
        recurrencePauseDao.findOpenPause(recurringExpenseId)
    override suspend fun findEligibleForMonth(referenceMonth: Int) =
        recurringExpenseDao.findEligibleForMonth(referenceMonth)
    override suspend fun insert(recurringExpense: RecurringExpenseEntity) =
        recurringExpenseDao.insert(recurringExpense)
    override suspend fun update(recurringExpense: RecurringExpenseEntity) =
        recurringExpenseDao.update(recurringExpense)
    override suspend fun confirmForMonth(expense: ExpenseEntryEntity) = expenseDao.insert(expense)
    override suspend fun pause(recurringExpenseId: Long, pausedAt: Long) =
        recurrencePauseDao.insert(
            RecurrencePauseEntity(
                recurringExpenseId = recurringExpenseId,
                pausedAt = pausedAt,
            ),
        )

    override suspend fun resume(
        recurringExpenseId: Long,
        resumedAt: Long,
        resumesFromMonth: Int,
    ): Boolean = database.withTransaction {
        val pause = recurrencePauseDao.findOpenPause(recurringExpenseId) ?: return@withTransaction false
        recurrencePauseDao.resume(pause.id, resumedAt, resumesFromMonth) > 0
    }

    override suspend fun disable(id: Long, disabledAt: Long) =
        recurringExpenseDao.disable(id, disabledAt) > 0
}
