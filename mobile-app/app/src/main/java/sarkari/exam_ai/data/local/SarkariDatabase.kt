package sarkari.exam_ai.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import sarkari.exam_ai.data.local.entities.SavedPdfEntity

@Database(entities = [SavedPdfEntity::class], version = 1, exportSchema = false)
abstract class SarkariDatabase : RoomDatabase() {

    abstract fun sarkariDao(): SarkariDao

    companion object {
        @Volatile
        private var INSTANCE: SarkariDatabase? = null

        fun getDatabase(context: Context): SarkariDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SarkariDatabase::class.java,
                    "sarkari_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
