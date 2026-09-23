package br.com.arthiviatech.myfinances.data.repository

import br.com.arthiviatech.myfinances.data.room.entitys.CardEntity
import kotlinx.coroutines.flow.Flow

interface CardRepository {
    fun observeEnabled(): Flow<List<CardEntity>>
    fun observeAll(): Flow<List<CardEntity>>
    suspend fun findById(id: Long): CardEntity?
    suspend fun insert(card: CardEntity): Long
    suspend fun update(card: CardEntity)
    suspend fun disable(id: Long, disabledAt: Long): Boolean
    suspend fun enable(id: Long): Boolean
}
