package sgnv.anubis.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import sgnv.anubis.app.data.model.AppGroup
import sgnv.anubis.app.data.model.ManagedApp

class AppGroupConverter {
    @TypeConverter
    fun fromAppGroup(group: AppGroup): String = group.name

    @TypeConverter
    fun toAppGroup(name: String): AppGroup = AppGroup.valueOf(name)
}

@Database(entities = [ManagedApp::class], version = 3, exportSchema = false)
@TypeConverters(AppGroupConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun managedAppDao(): ManagedAppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "vpn_stealth.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
