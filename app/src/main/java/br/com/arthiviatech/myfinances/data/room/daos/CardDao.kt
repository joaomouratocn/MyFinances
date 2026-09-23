package br.com.arthiviatech.myfinances.data.room.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import br.com.arthiviatech.myfinances.data.room.entitys.CardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {
    @Query("SELECT * FROM cards WHERE enabled = 1 ORDER BY name COLLATE NOCASE")
    fun observeEnabled(): Flow<List<CardEntity>>

    @Query("SELECT * FROM cards ORDER BY enabled DESC, name COLLATE NOCASE")
    fun observeAll(): Flow<List<CardEntity>>

    @Query("SELECT * FROM cards WHERE id = :id LIMIT 1")
    suspend fun findById(id: Long): CardEntity?

    @Insert
    suspend fun insert(card: CardEntity): Long

    @Update
    suspend fun update(card: CardEntity)

    @Query("UPDATE cards SET enabled = 0, disabled_at = :disabledAt WHERE id = :id")
    suspend fun disable(id: Long, disabledAt: Long): Int

    @Query("UPDATE cards SET enabled = 1, disabled_at = NULL WHERE id = :id")
    suspend fun enable(id: Long): Int
}
