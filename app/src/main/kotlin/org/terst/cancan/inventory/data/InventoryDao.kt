package org.terst.cancan.inventory.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface InventoryDao {
    @Query("SELECT * FROM inventory_items ORDER BY CASE WHEN expiry_date IS NULL THEN 1 ELSE 0 END, expiry_date ASC, name ASC")
    fun getAll(): Flow<List<InventoryItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: InventoryItemEntity)

    @Delete
    suspend fun delete(item: InventoryItemEntity)
}
