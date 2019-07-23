package com.example.android.data.db

import androidx.room.*
import com.example.android.data.model.Category
import io.reactivex.Observable
import io.reactivex.Single

@Dao
abstract class CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(categories: List<Category>)

    @Query("SELECT * FROM Category WHERE depth = 0")
    abstract fun getTopCategories(): Observable<List<Category>>

    @Query("SELECT * FROM Category WHERE parentId = :parentId")
    abstract fun getCategoriesByParent(parentId: String): Single<List<Category>>

    @Query("DELETE FROM Category")
    abstract fun deleteAll()

    @Query("SELECT * FROM Category WHERE id = :id LIMIT 1")
    abstract fun getCategoryById(id: String): Category?

    @Transaction
    open fun insertAll(categories: List<Category>) {
        deleteAll()
        insert(categories)
    }
}