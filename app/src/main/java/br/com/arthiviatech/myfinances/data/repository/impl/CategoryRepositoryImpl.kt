package br.com.arthiviatech.myfinances.data.repository.impl

import br.com.arthiviatech.myfinances.data.repository.CategoryRepository
import br.com.arthiviatech.myfinances.data.room.daos.CategoryDao
import br.com.arthiviatech.myfinances.data.room.entitys.CategoryEntity

class CategoryRepositoryImpl(
    private val categoryDao: CategoryDao,
) : CategoryRepository {
    override fun observeEnabled() = categoryDao.observeEnabled()
    override fun observeAll() = categoryDao.observeAll()
    override suspend fun findById(id: Long) = categoryDao.findById(id)
    override suspend fun insert(category: CategoryEntity) = categoryDao.insert(category)
    override suspend fun update(category: CategoryEntity) = categoryDao.update(category)
    override suspend fun disable(id: Long, disabledAt: Long) = categoryDao.disable(id, disabledAt) > 0
    override suspend fun enable(id: Long) = categoryDao.enable(id) > 0
}
