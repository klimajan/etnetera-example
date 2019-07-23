package com.example.android.di

import com.example.android.ui.activity.MainActivity
import com.example.android.ui.category.CategoryActivity
import com.example.android.ui.category.CategoryFragment
import com.example.android.ui.category.CategoryListFragment
import com.example.android.ui.filter.FilterFragment
import com.example.android.ui.filter.FilterStepperFragment
import com.example.android.ui.filter.SortingDialog
import dagger.Module
import dagger.android.AndroidInjectionModule
import dagger.android.ContributesAndroidInjector
import dagger.android.support.AndroidSupportInjectionModule

@Module(includes = [(AndroidInjectionModule::class), (AndroidSupportInjectionModule::class)])
abstract class BuildersModule {

    /**
     * Methods for binding viewModel activities
     */
    @ContributesAndroidInjector
    abstract fun bindMainActivity(): MainActivity


    /**
     * Category
     */
    @ContributesAndroidInjector
    abstract fun injectCategoryListFragment(): CategoryListFragment

    @ContributesAndroidInjector
    abstract fun injectCategoryActivity(): CategoryActivity

    @ContributesAndroidInjector
    abstract fun injectCategoryFragment(): CategoryFragment

    /**
     * Filter
     */
    @ContributesAndroidInjector
    abstract fun injectFilterFragment(): FilterFragment

    @ContributesAndroidInjector
    abstract fun injectFilterStepperFragment(): FilterStepperFragment

    @ContributesAndroidInjector
    abstract fun injectSortDialog(): SortingDialog
}