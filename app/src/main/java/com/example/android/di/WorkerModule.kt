package com.example.android.di

import com.example.android.data.repository.CategoryRepository
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class WorkerModule {

    @Binds
    @IntoMap
    @WorkerKey(CategoryRepository.CategoryWork::class)
    abstract fun bindConfigurationWorkFactory(factory: CategoryRepository.CategoryWork.Factory): ChildWorkerFactory


    @Binds
    abstract fun bindWorkerFactory(factory: WorkerFactory): androidx.work.WorkerFactory
}