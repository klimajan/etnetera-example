package com.example.android.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.android.data.model.*

@TypeConverters(Converters::class)
@Database(entities = [Category::class], version = 1, exportSchema = true)
abstract class Database : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao

}
