package com.thepanel.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [AlarmEntity::class, WeatherCacheEntity::class],
    version = 2,
    exportSchema = false
)
abstract class PanelDatabase : RoomDatabase() {
    abstract fun panelDao(): PanelDao

    companion object {
        fun create(context: Context): PanelDatabase {
            return Room.databaseBuilder(
                context,
                PanelDatabase::class.java,
                "the-panel.db"
            ).fallbackToDestructiveMigration().build()
        }
    }
}
