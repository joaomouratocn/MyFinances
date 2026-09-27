package br.com.arthiviatech.myfinances.data.room.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import br.com.arthiviatech.myfinances.data.room.entitys.RecurrencePauseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecurrencePauseDao {
    @Query("SELECT * FROM recurrence_pauses ORDER BY paused_at")
    fun observeAll(): Flow<List<RecurrencePauseEntity>>

    @Query("SELECT * FROM recurrence_pauses WHERE recurring_expense_id = :recurringExpenseId ORDER BY paused_at")
    suspend fun findAllByRecurringExpense(recurringExpenseId: Long): List<RecurrencePauseEntity>

    @Query(
        """
        SELECT * FROM recurrence_pauses
        WHERE recurring_expense_id = :recurringExpenseId
        ORDER BY paused_at DESC
        """,
    )
    fun observeByRecurringExpense(recurringExpenseId: Long): Flow<List<RecurrencePauseEntity>>

    @Query(
        """
        SELECT * FROM recurrence_pauses
        WHERE recurring_expense_id = :recurringExpenseId AND resumed_at IS NULL
        LIMIT 1
        """,
    )
    suspend fun findOpenPause(recurringExpenseId: Long): RecurrencePauseEntity?

    @Insert
    suspend fun insert(pause: RecurrencePauseEntity): Long

    @Query(
        """
        UPDATE recurrence_pauses
        SET resumed_at = :resumedAt, resumes_from_month = :resumesFromMonth
        WHERE id = :pauseId AND resumed_at IS NULL
        """,
    )
    suspend fun resume(
        pauseId: Long,
        resumedAt: Long,
        resumesFromMonth: Int,
    ): Int
}
