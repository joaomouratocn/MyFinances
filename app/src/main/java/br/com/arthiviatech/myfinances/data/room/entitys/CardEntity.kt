package br.com.arthiviatech.myfinances.data.room.entitys

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cards",
    indices = [Index(value = ["name"], unique = true)],
)
data class CardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    @ColumnInfo(name = "due_day")
    val dueDay: Int,
    @ColumnInfo(name = "closing_day")
    val closingDay: Int? = null,
    val enabled: Boolean = true,
    @ColumnInfo(name = "disabled_at")
    val disabledAt: Long? = null,
)
