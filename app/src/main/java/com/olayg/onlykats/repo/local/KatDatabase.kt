package com.olayg.onlykats.repo.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.olayg.onlykats.model.Kat
import com.olayg.onlykats.repo.local.dao.KatDao
import com.olayg.onlykats.repo.local.util.KatConverters


@Database(entities = [Kat::class], version = 1, exportSchema = false)
@TypeConverters(KatConverters::class)
abstract class KatDatabase: RoomDatabase() {


    //abstract fun katDao(): KatDao
    abstract val katDao: KatDao

    companion object {

        private const val DATABASE_NAME = "kat.db"
        private var INSTANCE: KatDatabase? = null

        fun getInstance(context: Context): KatDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        KatDatabase::class.java,
                        DATABASE_NAME
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }

}