package org.terst.cancan.inventory.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [InventoryItemEntity::class], version = 1, exportSchema = false)
abstract class CanCanDatabase : RoomDatabase() {
    abstract fun inventoryDao(): InventoryDao
}
