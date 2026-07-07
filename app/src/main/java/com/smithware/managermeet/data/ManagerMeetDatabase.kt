package com.smithware.managermeet.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [ProjectEntity::class, SettingsEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ManagerMeetDatabase : RoomDatabase() {
    abstract fun dao(): ManagerMeetDao

    companion object {
        @Volatile private var instance: ManagerMeetDatabase? = null

        fun get(context: Context): ManagerMeetDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    ManagerMeetDatabase::class.java,
                    "managermeet.db"
                ).build().also { instance = it }
            }
    }
}
