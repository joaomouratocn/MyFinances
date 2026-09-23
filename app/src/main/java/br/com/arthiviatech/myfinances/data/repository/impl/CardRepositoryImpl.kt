package br.com.arthiviatech.myfinances.data.repository.impl

import br.com.arthiviatech.myfinances.data.repository.CardRepository
import br.com.arthiviatech.myfinances.data.room.daos.CardDao
import br.com.arthiviatech.myfinances.data.room.entitys.CardEntity

class CardRepositoryImpl(
    private val cardDao: CardDao,
) : CardRepository {
    override fun observeEnabled() = cardDao.observeEnabled()
    override fun observeAll() = cardDao.observeAll()
    override suspend fun findById(id: Long) = cardDao.findById(id)
    override suspend fun insert(card: CardEntity) = cardDao.insert(card)
    override suspend fun update(card: CardEntity) = cardDao.update(card)
    override suspend fun disable(id: Long, disabledAt: Long) = cardDao.disable(id, disabledAt) > 0
    override suspend fun enable(id: Long) = cardDao.enable(id) > 0
}
