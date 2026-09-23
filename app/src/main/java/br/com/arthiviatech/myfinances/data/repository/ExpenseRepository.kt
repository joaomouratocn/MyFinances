package br.com.arthiviatech.myfinances.data.repository

import br.com.arthiviatech.myfinances.data.room.entitys.ExpenseEntryEntity
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    fun observeByMonth(referenceMonth: Int): Flow<List<ExpenseEntryEntity>>
    fun observeByInstallmentPlan(installmentPlanId: Long): Flow<List<ExpenseEntryEntity>>
    suspend fun findById(id: Long): ExpenseEntryEntity?
    suspend fun findByRecurrenceAndMonth(
        recurringExpenseId: Long,
        referenceMonth: Int,
    ): ExpenseEntryEntity?
    suspend fun insert(expense: ExpenseEntryEntity): Long
    suspend fun insertAll(expenses: List<ExpenseEntryEntity>): List<Long>
    suspend fun update(expense: ExpenseEntryEntity)
    suspend fun markAsPaid(id: Long, paidAt: Long): Boolean
    suspend fun markAsPending(id: Long): Boolean
    suspend fun disable(id: Long, disabledAt: Long): Boolean
    suspend fun disableInstallmentsFrom(
        installmentPlanId: Long,
        fromInstallmentNumber: Int,
        disabledAt: Long,
    ): Int
}
