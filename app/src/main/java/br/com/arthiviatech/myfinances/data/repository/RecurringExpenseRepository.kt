package br.com.arthiviatech.myfinances.data.repository

import br.com.arthiviatech.myfinances.data.room.entitys.ExpenseEntryEntity
import br.com.arthiviatech.myfinances.data.room.entitys.RecurrencePauseEntity
import br.com.arthiviatech.myfinances.data.room.entitys.RecurringExpenseEntity
import kotlinx.coroutines.flow.Flow

interface RecurringExpenseRepository {
    fun observeEnabled(): Flow<List<RecurringExpenseEntity>>
    fun observeAll(): Flow<List<RecurringExpenseEntity>>
    fun observePauses(recurringExpenseId: Long): Flow<List<RecurrencePauseEntity>>
    fun observeAllPauses(): Flow<List<RecurrencePauseEntity>>
    suspend fun findById(id: Long): RecurringExpenseEntity?
    suspend fun findOpenPause(recurringExpenseId: Long): RecurrencePauseEntity?
    suspend fun findPauses(recurringExpenseId: Long): List<RecurrencePauseEntity>
    suspend fun findEligibleForMonth(referenceMonth: Int): List<RecurringExpenseEntity>
    suspend fun insert(recurringExpense: RecurringExpenseEntity): Long
    suspend fun update(recurringExpense: RecurringExpenseEntity)
    suspend fun confirmForMonth(expense: ExpenseEntryEntity): Long
    suspend fun pause(recurringExpenseId: Long, pausedAt: Long): Long
    suspend fun resume(
        recurringExpenseId: Long,
        resumedAt: Long,
        resumesFromMonth: Int,
    ): Boolean
    suspend fun disable(id: Long, disabledAt: Long): Boolean
}
