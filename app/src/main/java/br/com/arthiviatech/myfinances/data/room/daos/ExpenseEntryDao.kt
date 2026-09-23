package br.com.arthiviatech.myfinances.data.room.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import br.com.arthiviatech.myfinances.data.room.entitys.ExpenseEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseEntryDao {
    @Query(
        """
        SELECT * FROM expense_entries
        WHERE reference_month = :referenceMonth AND enabled = 1
        ORDER BY due_date_epoch_day, description COLLATE NOCASE
        """,
    )
    fun observeByMonth(referenceMonth: Int): Flow<List<ExpenseEntryEntity>>

    @Query("SELECT * FROM expense_entries WHERE id = :id LIMIT 1")
    suspend fun findById(id: Long): ExpenseEntryEntity?

    @Query(
        """
        SELECT * FROM expense_entries
        WHERE installment_plan_id = :installmentPlanId
        ORDER BY installment_number
        """,
    )
    fun observeByInstallmentPlan(installmentPlanId: Long): Flow<List<ExpenseEntryEntity>>

    @Query(
        """
        SELECT * FROM expense_entries
        WHERE recurring_expense_id = :recurringExpenseId
          AND reference_month = :referenceMonth
        LIMIT 1
        """,
    )
    suspend fun findByRecurrenceAndMonth(
        recurringExpenseId: Long,
        referenceMonth: Int,
    ): ExpenseEntryEntity?

    @Insert
    suspend fun insert(expense: ExpenseEntryEntity): Long

    @Insert
    suspend fun insertAll(expenses: List<ExpenseEntryEntity>): List<Long>

    @Update
    suspend fun update(expense: ExpenseEntryEntity)

    @Query("UPDATE expense_entries SET paid_at = :paidAt WHERE id = :id AND enabled = 1")
    suspend fun markAsPaid(id: Long, paidAt: Long): Int

    @Query("UPDATE expense_entries SET paid_at = NULL WHERE id = :id AND enabled = 1")
    suspend fun markAsPending(id: Long): Int

    @Query("UPDATE expense_entries SET enabled = 0, disabled_at = :disabledAt WHERE id = :id")
    suspend fun disable(id: Long, disabledAt: Long): Int
}
