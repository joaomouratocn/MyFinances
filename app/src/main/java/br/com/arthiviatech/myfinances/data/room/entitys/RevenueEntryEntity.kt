package br.com.arthiviatech.myfinances.data.room.entitys

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "revenue_entries",
    foreignKeys = [
        ForeignKey(
            entity = FixedRevenueEntity::class,
            parentColumns = ["id"],
            childColumns = ["fixed_revenue_id"],
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
        ),
    ],
    indices = [
        Index(value = ["fixed_revenue_id", "reference_month"], unique = true),
        Index("category_id"),
        Index("expected_date_epoch_day"),
    ],
)
data class RevenueEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val description: String,
    @ColumnInfo(name = "amount_cents")
    val amountCents: Long,
    @ColumnInfo(name = "expected_date_epoch_day")
    val expectedDateEpochDay: Long,
    @ColumnInfo(name = "reference_month")
    val referenceMonth: Int,
    val origin: String,
    @ColumnInfo(name = "fixed_revenue_id")
    val fixedRevenueId: Long? = null,
    @ColumnInfo(name = "category_id")
    val categoryId: Long? = null,
    val enabled: Boolean = true,
    @ColumnInfo(name = "disabled_at")
    val disabledAt: Long? = null,
)
