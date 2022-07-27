package com.example.carservice

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [BrandTable::class, ModelTable::class, CarsItemTable::class], version = 2, exportSchema = true, autoMigrations = [AutoMigration(from = 1, to = 2)])
abstract class AppDataBase : RoomDatabase() {
    abstract fun dao(): CarsDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getDatabase(context: Context): AppDataBase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "_database.db"
                ).createFromAsset("data_base/data_base_cars.db")
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}