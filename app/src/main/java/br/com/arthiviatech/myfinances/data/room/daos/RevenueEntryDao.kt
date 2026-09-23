package br.com.arthiviatech.myfinances.data.room.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import br.com.arthiviatech.myfinances.data.room.entitys.RevenueEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RevenueEntryDao {
    @Query(
        """
        SELECT * FROM revenue_entries
        WHERE reference_month = :referenceMonth AND enabled = 1
        ORDER BY expected_date_epoch_day, description COLLATE NOCASE
        """,
    )
    fun observeByMonth(referenceMonth: Int): Flow<List<RevenueEntryEntity>>

    @Query("SELECT * FROM revenue_entries WHERE id = :id LIMIT 1")
    suspend fun findById(id: Long): RevenueEntryEntity?

    @Query(
        """
        SELECT * FROM revenue_entries
        WHERE fixed_revenue_id = :fixedRevenueId AND reference_month = :referenceMonth
        LIMIT 1
        """,
    )
    suspend fun findByFixedRevenueAndMonth(
        fixedRevenueId: Long,
        referenceMonth: Int,
    ): RevenueEntryEntity?

    @Insert
    suspend fun insert(revenue: RevenueEntryEntity): Long

    @Insert
    suspend fun insertAll(revenues: List<RevenueEntryEntity>): List<Long>

    @Update
    suspend fun update(revenue: RevenueEntryEntity)

    @Query("UPDATE revenue_entries SET enabled = 0, disabled_at = :disabledAt WHERE id = :id")
    suspend fun disable(id: Long, disabledAt: Long): Int
}
