package com.example.android.di

import androidx.room.Room
import android.content.Context
import com.example.android.data.db.*
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(context: Context): Database {
        return Room.databaseBuilder(context, Database::class.java, "db")
                .addMigrations()
                .allowMainThreadQueries()
                .build()
    }


    @Singleton
    @Provides
    fun provideCategoryDao(database: Database): CategoryDao {
        return database.categoryDao()
    }
}