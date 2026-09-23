package br.com.arthiviatech.myfinances.data.repository.impl

import androidx.room.withTransaction
import br.com.arthiviatech.myfinances.data.repository.RevenueRepository
import br.com.arthiviatech.myfinances.data.room.AppDatabase
import br.com.arthiviatech.myfinances.data.room.daos.FixedRevenueDao
import br.com.arthiviatech.myfinances.data.room.daos.FixedRevenueVersionDao
import br.com.arthiviatech.myfinances.data.room.daos.RevenueEntryDao
import br.com.arthiviatech.myfinances.data.room.entitys.FixedRevenueEntity
import br.com.arthiviatech.myfinances.data.room.entitys.FixedRevenueVersionEntity
import br.com.arthiviatech.myfinances.data.room.entitys.RevenueEntryEntity

class RevenueRepositoryImpl(
    private val database: AppDatabase,
    private val revenueEntryDao: RevenueEntryDao,
    private val fixedRevenueDao: FixedRevenueDao,
    private val fixedRevenueVersionDao: FixedRevenueVersionDao,
) : RevenueRepository {
    override fun observeByMonth(referenceMonth: Int) = revenueEntryDao.observeByMonth(referenceMonth)
    override fun observeFixedRevenues() = fixedRevenueDao.observeEnabled()
    override fun observeVersions(fixedRevenueId: Long) =
        fixedRevenueVersionDao.observeByFixedRevenue(fixedRevenueId)
    override suspend fun findEntryById(id: Long) = revenueEntryDao.findById(id)
    override suspend fun findFixedRevenueById(id: Long) = fixedRevenueDao.findById(id)
    override suspend fun findFixedRevenuesEligibleForMonth(referenceMonth: Int) =
        fixedRevenueDao.findEligibleForMonth(referenceMonth)
    override suspend fun findFixedEntryForMonth(fixedRevenueId: Long, referenceMonth: Int) =
        revenueEntryDao.findByFixedRevenueAndMonth(fixedRevenueId, referenceMonth)
    override suspend fun findEffectiveVersion(fixedRevenueId: Long, referenceMonth: Int) =
        fixedRevenueVersionDao.findEffectiveVersion(fixedRevenueId, referenceMonth)
    override suspend fun insertEntry(revenue: RevenueEntryEntity) = revenueEntryDao.insert(revenue)
    override suspend fun insertEntries(revenues: List<RevenueEntryEntity>) =
        revenueEntryDao.insertAll(revenues)
    override suspend fun updateEntry(revenue: RevenueEntryEntity) = revenueEntryDao.update(revenue)

    override suspend fun createFixedRevenue(
        fixedRevenue: FixedRevenueEntity,
        initialVersion: FixedRevenueVersionEntity,
    ): Long = database.withTransaction {
        val fixedRevenueId = fixedRevenueDao.insert(fixedRevenue)
        fixedRevenueVersionDao.insert(initialVersion.copy(fixedRevenueId = fixedRevenueId))
        fixedRevenueId
    }

    override suspend fun insertVersion(version: FixedRevenueVersionEntity) =
        fixedRevenueVersionDao.insert(version)
    override suspend fun disableEntry(id: Long, disabledAt: Long) =
        revenueEntryDao.disable(id, disabledAt) > 0
    override suspend fun disableFixedRevenue(id: Long, disabledAt: Long) =
        fixedRevenueDao.disable(id, disabledAt) > 0
}
