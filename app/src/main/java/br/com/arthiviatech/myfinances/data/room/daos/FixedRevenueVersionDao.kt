package br.com.arthiviatech.myfinances.data.room.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import br.com.arthiviatech.myfinances.data.room.entitys.FixedRevenueVersionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FixedRevenueVersionDao {
    @Query(
        """
        SELECT * FROM fixed_revenue_versions
        WHERE fixed_revenue_id = :fixedRevenueId
        ORDER BY effective_from_month DESC
        """,
    )
    fun observeByFixedRevenue(fixedRevenueId: Long): Flow<List<FixedRevenueVersionEntity>>

    @Query(
        """
        SELECT * FROM fixed_revenue_versions
        WHERE fixed_revenue_id = :fixedRevenueId
          AND effective_from_month <= :referenceMonth
        ORDER BY effective_from_month DESC
        LIMIT 1
        """,
    )
    suspend fun findEffectiveVersion(
        fixedRevenueId: Long,
        referenceMonth: Int,
    ): FixedRevenueVersionEntity?

    @Insert
    suspend fun insert(version: FixedRevenueVersionEntity): Long

    @Update
    suspend fun update(version: FixedRevenueVersionEntity)
}
