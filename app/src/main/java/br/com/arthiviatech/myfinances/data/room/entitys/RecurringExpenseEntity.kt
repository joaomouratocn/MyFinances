package br.com.arthiviatech.myfinances.data.room.entitys

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "recurring_expenses",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
        ),
        ForeignKey(
            entity = CardEntity::class,
            parentColumns = ["id"],
            childColumns = ["card_id"],
        ),
    ],
    indices = [Index("category_id"), Index("card_id")],
)
data class RecurringExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val description: String,
    @ColumnInfo(name = "category_id")
    val categoryId: Long,
    @ColumnInfo(name = "due_day")
    val dueDay: Int,
    @ColumnInfo(name = "payment_method")
    val paymentMethod: String,
    @ColumnInfo(name = "card_id")
    val cardId: Long? = null,
    @ColumnInfo(name = "start_month")
    val startMonth: Int,
    val enabled: Boolean = true,
    @ColumnInfo(name = "disabled_at")
    val disabledAt: Long? = null,
)
