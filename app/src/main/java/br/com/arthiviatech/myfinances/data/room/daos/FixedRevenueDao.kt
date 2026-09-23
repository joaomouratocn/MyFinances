package br.com.arthiviatech.myfinances.data.room.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import br.com.arthiviatech.myfinances.data.room.entitys.FixedRevenueEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FixedRevenueDao {
    @Query("SELECT * FROM fixed_revenues WHERE enabled = 1 ORDER BY description COLLATE NOCASE")
    fun observeEnabled(): Flow<List<FixedRevenueEntity>>

    @Query("SELECT * FROM fixed_revenues ORDER BY enabled DESC, description COLLATE NOCASE")
    fun observeAll(): Flow<List<FixedRevenueEntity>>

    @Query("SELECT * FROM fixed_revenues WHERE id = :id LIMIT 1")
    suspend fun findById(id: Long): FixedRevenueEntity?

    @Query("SELECT * FROM fixed_revenues WHERE enabled = 1 AND start_month <= :referenceMonth")
    suspend fun findEligibleForMonth(referenceMonth: Int): List<FixedRevenueEntity>

    @Insert
    suspend fun insert(fixedRevenue: FixedRevenueEntity): Long

    @Update
    suspend fun update(fixedRevenue: FixedRevenueEntity)

    @Query("UPDATE fixed_revenues SET enabled = 0, disabled_at = :disabledAt WHERE id = :id")
    suspend fun disable(id: Long, disabledAt: Long): Int
}
