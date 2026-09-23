package br.com.arthiviatech.myfinances.data.room.entitys

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "fixed_revenue_versions",
    foreignKeys = [
        ForeignKey(
            entity = FixedRevenueEntity::class,
            parentColumns = ["id"],
            childColumns = ["fixed_revenue_id"],
        ),
    ],
    indices = [
        Index(
            value = ["fixed_revenue_id", "effective_from_month"],
            unique = true,
        ),
    ],
)
data class FixedRevenueVersionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "fixed_revenue_id")
    val fixedRevenueId: Long,
    @ColumnInfo(name = "default_amount_cents")
    val defaultAmountCents: Long,
    @ColumnInfo(name = "expected_day")
    val expectedDay: Int,
    @ColumnInfo(name = "effective_from_month")
    val effectiveFromMonth: Int,
)
