package com.example.android.data.repository

import android.content.Context
import androidx.work.RxWorker
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.android.data.db.CategoryDao
import com.example.android.data.model.Category
import com.example.android.data.model.Filter
import com.example.android.data.remote.RemoteService
import com.example.android.di.ChildWorkerFactory
import com.example.android.utility.Logcat
import com.example.android.utils.beginOneTimeUniqueWorkRequest
import com.example.android.utils.composeSchedulers
import com.example.android.utils.toListenableResult
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(private val service: RemoteService, private val dao: CategoryDao, private val workManager: WorkManager) {

    fun fetchCategoryFilters(categoryId: String, filters: String?): Single<List<Filter>> {
        return service.getCategoryFilters(categoryId, filters)
                .map { it.filters }
                .composeSchedulers()
    }

    private fun fetchCategories(): Completable {
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

    fun scheduleFetchCategories() {
        workManager.beginOneTimeUniqueWorkRequest(CategoryWork::class.java).enqueue()
    }

    fun getTopLevelCategories(): Observable<List<Category>> {
        return dao.getTopCategories()
                .composeSchedulers()
    }

    fun getCategoriesByParent(parentId: String): Single<List<Category>> {
        return dao.getCategoriesByParent(parentId)
                .composeSchedulers()
    }

    class CategoryWork @AssistedInject constructor(@Assisted context: Context, @Assisted params: WorkerParameters, private val repo: CategoryRepository) : RxWorker(context, params) {
        override fun createWork(): Single<Result> {
            return repo.fetchCategories().toListenableResult()
        }

        @AssistedInject.Factory
        interface Factory: ChildWorkerFactory
    }
}