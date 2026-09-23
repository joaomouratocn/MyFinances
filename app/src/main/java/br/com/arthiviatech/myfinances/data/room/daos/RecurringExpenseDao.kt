package br.com.arthiviatech.myfinances.data.room.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import br.com.arthiviatech.myfinances.data.room.entitys.RecurringExpenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecurringExpenseDao {
    @Query("SELECT * FROM recurring_expenses WHERE enabled = 1 ORDER BY due_day, description COLLATE NOCASE")
    fun observeEnabled(): Flow<List<RecurringExpenseEntity>>

    @Query("SELECT * FROM recurring_expenses ORDER BY enabled DESC, due_day, description COLLATE NOCASE")
    fun observeAll(): Flow<List<RecurringExpenseEntity>>

    @Query("SELECT * FROM recurring_expenses WHERE id = :id LIMIT 1")
    suspend fun findById(id: Long): RecurringExpenseEntity?

    @Query(
        """
        SELECT * FROM recurring_expenses
        WHERE enabled = 1 AND start_month <= :referenceMonth
        ORDER BY due_day, description COLLATE NOCASE
        """,
    )
    suspend fun findEligibleForMonth(referenceMonth: Int): List<RecurringExpenseEntity>

    @Insert
    suspend fun insert(recurringExpense: RecurringExpenseEntity): Long

    @Update
    suspend fun update(recurringExpense: RecurringExpenseEntity)

    @Query("UPDATE recurring_expenses SET enabled = 0, disabled_at = :disabledAt WHERE id = :id")
    suspend fun disable(id: Long, disabledAt: Long): Int

    @Query("UPDATE recurring_expenses SET enabled = 1, disabled_at = NULL WHERE id = :id")
    suspend fun enable(id: Long): Int
}
