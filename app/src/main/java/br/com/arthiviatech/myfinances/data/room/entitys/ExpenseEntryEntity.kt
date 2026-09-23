package br.com.arthiviatech.myfinances.data.room.entitys

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "expense_entries",
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
        ForeignKey(
            entity = InstallmentPlanEntity::class,
            parentColumns = ["id"],
            childColumns = ["installment_plan_id"],
        ),
        ForeignKey(
            entity = RecurringExpenseEntity::class,
            parentColumns = ["id"],
            childColumns = ["recurring_expense_id"],
        ),
    ],
    indices = [
        Index("category_id"),
        Index("card_id"),
        Index(value = ["installment_plan_id", "installment_number"], unique = true),
        Index(value = ["recurring_expense_id", "reference_month"], unique = true),
        Index("due_date_epoch_day"),
    ],
)
data class ExpenseEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val description: String,
    @ColumnInfo(name = "amount_cents")
    val amountCents: Long,
    @ColumnInfo(name = "category_id")
    val categoryId: Long,
    @ColumnInfo(name = "purchase_date_epoch_day")
    val purchaseDateEpochDay: Long,
    @ColumnInfo(name = "due_date_epoch_day")
    val dueDateEpochDay: Long,
    @ColumnInfo(name = "reference_month")
    val referenceMonth: Int,
    @ColumnInfo(name = "payment_method")
    val paymentMethod: String,
    @ColumnInfo(name = "card_id")
    val cardId: Long? = null,
    @ColumnInfo(name = "paid_at")
    val paidAt: Long? = null,
    val origin: String,
    @ColumnInfo(name = "installment_plan_id")
    val installmentPlanId: Long? = null,
    @ColumnInfo(name = "installment_number")
    val installmentNumber: Int? = null,
    @ColumnInfo(name = "installment_count")
    val installmentCount: Int? = null,
    @ColumnInfo(name = "recurring_expense_id")
    val recurringExpenseId: Long? = null,
    val enabled: Boolean = true,
    @ColumnInfo(name = "disabled_at")
    val disabledAt: Long? = null,
)
