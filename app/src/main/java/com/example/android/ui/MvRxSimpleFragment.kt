package com.example.android.ui

import android.content.Context
import android.os.Bundle
import com.airbnb.mvrx.BaseMvRxFragment
import dagger.android.support.AndroidSupportInjection

abstract class MvRxSimpleFragment : BaseMvRxFragment() {

    abstract fun appLifecycleObserver(): AppLifecycleObserver

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(appLifecycleObserver())
    }

    override fun invalidate() {}
}