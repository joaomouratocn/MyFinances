package br.com.arthiviatech.myfinances.data.room.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import br.com.arthiviatech.myfinances.data.room.entitys.InstallmentPlanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InstallmentPlanDao {
    @Query("SELECT * FROM installment_plans WHERE enabled = 1 ORDER BY first_due_date_epoch_day DESC")
    fun observeEnabled(): Flow<List<InstallmentPlanEntity>>

    @Query("SELECT * FROM installment_plans WHERE id = :id LIMIT 1")
    suspend fun findById(id: Long): InstallmentPlanEntity?

    @Insert
    suspend fun insert(plan: InstallmentPlanEntity): Long

    @Update
    suspend fun update(plan: InstallmentPlanEntity)

    @Query("UPDATE installment_plans SET enabled = 0, disabled_at = :disabledAt WHERE id = :id")
    suspend fun disable(id: Long, disabledAt: Long): Int
}
