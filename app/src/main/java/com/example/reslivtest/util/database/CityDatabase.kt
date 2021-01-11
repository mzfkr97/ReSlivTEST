package com.example.reslivtest.util.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [CityData::class, LocationResponse::class],
    version = 1,
    exportSchema = false)
    @TypeConverters(CityTypeConverters::class)

abstract class CityDatabase: RoomDatabase() {

    abstract fun cityDao(): CityDao

    companion object {
        @Volatile
        private var INSTANCE: CityDatabase? = null
        private var LOCK = Any()
        operator fun invoke (context: Context) = INSTANCE ?: synchronized(LOCK){
            INSTANCE ?: createDataBase(context).also { INSTANCE = it }
        }

        private fun createDataBase(context: Context)=
                Room.databaseBuilder(
                        context.applicationContext,
                        CityDatabase::class.java,
                        DATABASE_NAME
                ).build()


        private const val DATABASE_NAME = "res_database"
    }
}