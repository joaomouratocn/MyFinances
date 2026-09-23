package br.com.arthiviatech.myfinances.data.repository

import br.com.arthiviatech.myfinances.data.room.entitys.ExpenseEntryEntity
import br.com.arthiviatech.myfinances.data.room.entitys.InstallmentPlanEntity
import kotlinx.coroutines.flow.Flow

interface InstallmentRepository {
    fun observeEnabled(): Flow<List<InstallmentPlanEntity>>
    suspend fun findById(id: Long): InstallmentPlanEntity?
    suspend fun create(
        plan: InstallmentPlanEntity,
        installments: List<ExpenseEntryEntity>,
    ): Long
    suspend fun update(plan: InstallmentPlanEntity)
    suspend fun disable(id: Long, disabledAt: Long): Boolean
}
