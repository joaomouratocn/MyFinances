package br.com.arthiviatech.myfinances.data.repository

import br.com.arthiviatech.myfinances.data.room.entitys.CategoryEntity
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun observeEnabled(): Flow<List<CategoryEntity>>
    fun observeAll(): Flow<List<CategoryEntity>>
    suspend fun findById(id: Long): CategoryEntity?
    suspend fun insert(category: CategoryEntity): Long
    suspend fun insertAll(categories: List<CategoryEntity>): List<Long>
    suspend fun update(category: CategoryEntity)
    suspend fun disable(id: Long, disabledAt: Long): Boolean
    suspend fun enable(id: Long): Boolean
}
