package com.example.haberuygulamasi.Database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.haberuygulamasi.converts.Converters
import com.example.haberuygulamasi.model.RoomModel
import com.example.haberuygulamasi.servis.NewsDao

@Database(entities = [RoomModel::class], version = 5) // Versiyon numarasını artırdık
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun newsDao(): NewsDao
}



