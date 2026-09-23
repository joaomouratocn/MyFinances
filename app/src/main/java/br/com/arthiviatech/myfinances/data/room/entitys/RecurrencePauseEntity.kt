package br.com.arthiviatech.myfinances.data.room.entitys

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "recurrence_pauses",
    foreignKeys = [
        ForeignKey(
            entity = RecurringExpenseEntity::class,
            parentColumns = ["id"],
            childColumns = ["recurring_expense_id"],
        ),
    ],
    indices = [Index("recurring_expense_id")],
)
data class RecurrencePauseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "recurring_expense_id")
    val recurringExpenseId: Long,
    @ColumnInfo(name = "paused_at")
    val pausedAt: Long,
    @ColumnInfo(name = "resumed_at")
    val resumedAt: Long? = null,
    @ColumnInfo(name = "resumes_from_month")
    val resumesFromMonth: Int? = null,
)
