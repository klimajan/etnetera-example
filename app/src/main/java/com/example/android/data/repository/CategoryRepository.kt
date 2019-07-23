package com.example.android.data.repository

import com.example.android.data.db.CategoryDao
import com.example.android.data.model.Category
import com.example.android.data.model.Filter
import com.example.android.data.remote.RemoteService
import com.example.android.utility.Logcat
import com.example.android.utils.composeSchedulers
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(private val service: RemoteService, private val dao: CategoryDao) {

    fun fetchCategoryFilters(categoryId: String, filters: String?): Single<List<Filter>> {
        return service.getCategoryFilters(categoryId, filters)
                .map { it.filters }
                .composeSchedulers()
    }

    fun fetchCategories(): Completable {
        return service.getCategories()
                .map { it.get() }
                .flatMap {
                    Observable.fromIterable(it)
                            .concatMapIterable { category -> category.flatten() }
                            .toList()
                }
                .doOnSuccess { dao.insertAll(it) }
                .doOnError { Logcat.d(it.toString()) }
                .ignoreElement()
                .composeSchedulers()
    }

    fun getTopLevelCategories(): Observable<List<Category>> {
        return dao.getTopCategories()
                .composeSchedulers()
    }

    fun getCategoriesByParent(parentId: String): Single<List<Category>> {
        return dao.getCategoriesByParent(parentId)
                .composeSchedulers()
    }
}