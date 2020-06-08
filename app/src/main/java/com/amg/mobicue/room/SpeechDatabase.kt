package com.amg.mobicue.room

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Speech::class],
    views = [SpeechItem::class],
    version = 1,
    exportSchema = false
)
abstract class SpeechDatabase : RoomDatabase() {
    abstract fun getSpeechDao(): SpeechDao

    companion object {

        @Volatile
        private var INSTANCE: SpeechDatabase? = null

        fun getInstance(application: Application): SpeechDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    application,
                    SpeechDatabase::class.java,
                    "speech_database"
                ).fallbackToDestructiveMigration()
                    .build()
            }
        }
    }
}