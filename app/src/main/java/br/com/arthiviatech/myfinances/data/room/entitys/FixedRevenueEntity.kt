package br.com.arthiviatech.myfinances.data.room.entitys

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "fixed_revenues",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
        ),
    ],
    indices = [Index("category_id")],
)
data class FixedRevenueEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val description: String,
    @ColumnInfo(name = "start_month")
    val startMonth: Int,
    @ColumnInfo(name = "category_id")
    val categoryId: Long? = null,
    val enabled: Boolean = true,
    @ColumnInfo(name = "disabled_at")
    val disabledAt: Long? = null,
)
