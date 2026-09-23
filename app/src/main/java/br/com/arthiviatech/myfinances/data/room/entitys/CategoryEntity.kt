package br.com.arthiviatech.myfinances.data.room.entitys

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "categories",
    indices = [Index(value = ["name"], unique = true)],
)
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    @ColumnInfo(name = "system_defined")
    val systemDefined: Boolean = false,
    val enabled: Boolean = true,
    @ColumnInfo(name = "disabled_at")
    val disabledAt: Long? = null,
)
