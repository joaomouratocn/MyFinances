package br.com.arthiviatech.myfinances.data.repository

import br.com.arthiviatech.myfinances.data.room.entitys.FixedRevenueEntity
import br.com.arthiviatech.myfinances.data.room.entitys.FixedRevenueVersionEntity
import br.com.arthiviatech.myfinances.data.room.entitys.RevenueEntryEntity
import kotlinx.coroutines.flow.Flow

interface RevenueRepository {
    fun observeByMonth(referenceMonth: Int): Flow<List<RevenueEntryEntity>>
    fun observeFixedRevenues(): Flow<List<FixedRevenueEntity>>
    fun observeVersions(fixedRevenueId: Long): Flow<List<FixedRevenueVersionEntity>>
    suspend fun findEntryById(id: Long): RevenueEntryEntity?
    suspend fun findFixedRevenueById(id: Long): FixedRevenueEntity?
    suspend fun findFixedRevenuesEligibleForMonth(referenceMonth: Int): List<FixedRevenueEntity>
    suspend fun findFixedEntryForMonth(fixedRevenueId: Long, referenceMonth: Int): RevenueEntryEntity?
    suspend fun findEffectiveVersion(fixedRevenueId: Long, referenceMonth: Int): FixedRevenueVersionEntity?
    suspend fun insertEntry(revenue: RevenueEntryEntity): Long
    suspend fun insertEntries(revenues: List<RevenueEntryEntity>): List<Long>
    suspend fun updateEntry(revenue: RevenueEntryEntity)
    suspend fun createFixedRevenue(
        fixedRevenue: FixedRevenueEntity,
        initialVersion: FixedRevenueVersionEntity,
    ): Long
    suspend fun insertVersion(version: FixedRevenueVersionEntity): Long
    suspend fun updateVersion(version: FixedRevenueVersionEntity)
    suspend fun disableEntry(id: Long, disabledAt: Long): Boolean
    suspend fun disableFixedRevenue(id: Long, disabledAt: Long): Boolean
}
