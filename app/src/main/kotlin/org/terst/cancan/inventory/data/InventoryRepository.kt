package org.terst.cancan.inventory.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InventoryRepository @Inject constructor(private val dao: InventoryDao) {
    val items: Flow<List<InventoryItemEntity>> = dao.getAll()
    suspend fun save(item: InventoryItemEntity) = dao.upsert(item)
    suspend fun delete(item: InventoryItemEntity) = dao.delete(item)
}
