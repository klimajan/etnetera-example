package com.example.android.utils

import android.annotation.SuppressLint
import androidx.work.*
import io.reactivex.Completable
import io.reactivex.Single

fun <T> Single<T>.toListenableResult(): Single<ListenableWorker.Result> {
    return this.map { ListenableWorker.Result.success() }
            .onErrorReturn { ListenableWorker.Result.failure() }
}

fun Completable.toListenableResult(): Single<ListenableWorker.Result> {
    return this.toSingleDefault(true).toListenableResult()
}

fun getOneTimeWorkRequest(workerClazz: Class<out RxWorker>, params: Data = Data.Builder().build()): OneTimeWorkRequest {
    val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

    return OneTimeWorkRequest.Builder(workerClazz)
            .setInputData(params)
            .setConstraints(constraints)
            .addTag(workerClazz.simpleName)
            .build()
}

@SuppressLint("EnqueueWork")
fun WorkManager.beginOneTimeUniqueWorkRequest(workerClazz: Class<out RxWorker>, tag: String = workerClazz.name, params: Data = Data.Builder().build()): WorkContinuation {
    val request = getOneTimeWorkRequest(workerClazz, params)
    return this.beginUniqueWork(tag, ExistingWorkPolicy.REPLACE, request)
}