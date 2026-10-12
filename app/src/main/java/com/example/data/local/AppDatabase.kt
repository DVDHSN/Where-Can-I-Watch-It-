package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.TitleDao
import com.example.data.local.dao.WatchlistDao
import com.example.data.local.entity.CachedTitleEntity
import com.example.data.local.entity.WatchlistEntity

@Database(
    entities = [
        CachedTitleEntity::class,
        WatchlistEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun titleDao(): TitleDao
    abstract fun watchlistDao(): WatchlistDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "where_can_i_watch_it.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
