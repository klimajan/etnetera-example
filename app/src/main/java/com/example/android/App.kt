package com.example.android

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Build

import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.multidex.MultiDex

import com.example.android.di.DaggerAppComponent
import com.example.android.utility.LocaleContextWrapper
import com.example.android.utility.Logcat

import java.util.Arrays

import javax.inject.Inject

import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.support.HasSupportFragmentInjector
import io.reactivex.plugins.RxJavaPlugins

class App : android.app.Application(), HasActivityInjector, HasSupportFragmentInjector {
    @Inject lateinit var dispatchingActivityInjector: DispatchingAndroidInjector<Activity>
    @Inject lateinit var dispatchingFragmentInjector: DispatchingAndroidInjector<Fragment>
    @Inject lateinit var lifecycleObserver: LifecycleObserver


    override fun onCreate() {
        super.onCreate()

        // Instantiate app component
        DaggerAppComponent.builder()
                .application(this)
                .build().inject(this)

        // Lifecycle observing
        ProcessLifecycleOwner.get()
                .lifecycle
                .addObserver(lifecycleObserver)

        // Support for vectorDrawable
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        // RxJava2 Global error handling
        RxJavaPlugins.setErrorHandler { e -> Logcat.d(e.toString()) }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        LocaleContextWrapper.wrap(this)
    }

    override fun attachBaseContext(base: Context) {
        if (!LocaleContextWrapper.hasPersistedLanguage(base)) {
            val language = getLanguage(base)
            if (listOf(*Config.APP_LOCALES).contains(language)) {
                LocaleContextWrapper.persistLanguage(base, language)
            } else
                LocaleContextWrapper.persistLanguage(base, LocaleContextWrapper.DEFAULT_LANGUAGE)
        }

        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    @Suppress("DEPRECATION")
    private fun getLanguage(context: Context): String {
        val config = context.resources.configuration
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.locales.get(0).language
        } else {
            config.locale.language
        }
    }

    override fun activityInjector(): AndroidInjector<Activity>? {
        return dispatchingActivityInjector
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment>? {
        return dispatchingFragmentInjector
    }
}
