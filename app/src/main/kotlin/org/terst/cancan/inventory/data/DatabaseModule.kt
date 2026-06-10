package org.terst.cancan.inventory.data

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): CanCanDatabase =
        Room.databaseBuilder(context, CanCanDatabase::class.java, "cancan.db").build()

    @Provides
    @Singleton
    fun provideInventoryDao(db: CanCanDatabase): InventoryDao = db.inventoryDao()
}
