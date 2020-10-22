package com.example.bazaar_hisabnew.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database (entities = [Lists::class, Item::class], version = 1)
abstract class Database : RoomDatabase() {
    abstract fun listDao() : ListsDao
    abstract fun itemDao() : ItemDao

    companion object{
        @Volatile private var instance : com.example.bazaar_hisabnew.db.Database? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance?: synchronized(LOCK){
            instance?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            com.example.bazaar_hisabnew.db.Database::class.java,
            "database"
        ).build()
    }
}