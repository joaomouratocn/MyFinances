package br.com.arthiviatech.myfinances.data.room.entitys

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "installment_plans",
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
data class InstallmentPlanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val description: String,
    @ColumnInfo(name = "installment_count")
    val installmentCount: Int,
    @ColumnInfo(name = "installment_amount_cents")
    val installmentAmountCents: Long,
    @ColumnInfo(name = "informational_total_cents")
    val informationalTotalCents: Long,
    @ColumnInfo(name = "first_due_date_epoch_day")
    val firstDueDateEpochDay: Long,
    @ColumnInfo(name = "category_id")
    val categoryId: Long,
    @ColumnInfo(name = "payment_method")
    val paymentMethod: String,
    @ColumnInfo(name = "card_id")
    val cardId: Long? = null,
    val enabled: Boolean = true,
    @ColumnInfo(name = "disabled_at")
    val disabledAt: Long? = null,
)
