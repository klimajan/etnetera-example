package com.example.android.ui

import com.airbnb.mvrx.BaseMvRxViewModel
import com.airbnb.mvrx.MvRxState
import com.example.android.Config
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class MvRxViewModel<S : MvRxState>(initialState: S) : BaseMvRxViewModel<S>(initialState, debugMode = Config.DEV_ENVIRONMENT), AppLifecycleObserver {
    private val onStopDisposables = CompositeDisposable()

    override fun onStop() {
        super.onStop()
        onStopDisposables.dispose()
    }

    protected fun Disposable.disposeOnStop(): Disposable {
        onStopDisposables.add(this)
        return this
    }

}