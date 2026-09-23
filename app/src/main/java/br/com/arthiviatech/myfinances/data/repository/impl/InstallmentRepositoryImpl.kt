package br.com.arthiviatech.myfinances.data.repository.impl

import androidx.room.withTransaction
import br.com.arthiviatech.myfinances.data.repository.InstallmentRepository
import br.com.arthiviatech.myfinances.data.room.AppDatabase
import br.com.arthiviatech.myfinances.data.room.daos.ExpenseEntryDao
import br.com.arthiviatech.myfinances.data.room.daos.InstallmentPlanDao
import br.com.arthiviatech.myfinances.data.room.entitys.ExpenseEntryEntity
import br.com.arthiviatech.myfinances.data.room.entitys.InstallmentPlanEntity

class InstallmentRepositoryImpl(
    private val database: AppDatabase,
    private val installmentPlanDao: InstallmentPlanDao,
    private val expenseDao: ExpenseEntryDao,
) : InstallmentRepository {
    override fun observeEnabled() = installmentPlanDao.observeEnabled()
    override suspend fun findById(id: Long) = installmentPlanDao.findById(id)

    override suspend fun create(
        plan: InstallmentPlanEntity,
        installments: List<ExpenseEntryEntity>,
    ): Long = database.withTransaction {
        val planId = installmentPlanDao.insert(plan)
        expenseDao.insertAll(installments.map { it.copy(installmentPlanId = planId) })
        planId
    }

    override suspend fun update(plan: InstallmentPlanEntity) = installmentPlanDao.update(plan)
    override suspend fun disable(id: Long, disabledAt: Long) =
        installmentPlanDao.disable(id, disabledAt) > 0
}
