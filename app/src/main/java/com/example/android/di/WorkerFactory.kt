package com.example.android.di

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import javax.inject.Inject
import javax.inject.Provider

class WorkerFactory @Inject constructor(private val workers: Map<Class<out ListenableWorker>, @JvmSuppressWildcards Provider<ChildWorkerFactory>>) : WorkerFactory() {

    override fun createWorker(appContext: Context, workerClassName: String, workerParameters: WorkerParameters): ListenableWorker? {
        return workers.entries.find { Class.forName(workerClassName).isAssignableFrom(it.key) }?.value?.get()?.create(appContext, workerParameters)
    }
}

interface ChildWorkerFactory {
    fun create(context: Context, params: WorkerParameters): ListenableWorker
}