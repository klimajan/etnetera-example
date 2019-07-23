package com.example.android.ui

import com.airbnb.epoxy.AsyncEpoxyController
import com.airbnb.epoxy.EpoxyController
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.withState

open class MvRxEpoxyController(val buildModelsCallback: EpoxyController.() -> Unit = {}) : AsyncEpoxyController() {

    override fun buildModels() {
        buildModelsCallback()
    }
}

fun MvRxFragment.simpleController(buildModels: EpoxyController.() -> Unit) = MvRxEpoxyController {
    // Models are built asynchronously, so it is possible that this is called after the fragment
    // is detached under certain race conditions.
    if (view == null || isRemoving) return@MvRxEpoxyController
    buildModels()
}

fun MvRxDialogFragment.simpleController(buildModels: EpoxyController.() -> Unit) = MvRxEpoxyController {
    // Models are built asynchronously, so it is possible that this is called after the fragment
    // is detached under certain race conditions.
    if (view == null || isRemoving) return@MvRxEpoxyController
    buildModels()
}

fun <S : MvRxState, A : MvRxViewModel<S>> MvRxFragment.simpleController(viewModel: A, buildModels: EpoxyController.(state: S) -> Unit) = MvRxEpoxyController {
    if (view == null || isRemoving) return@MvRxEpoxyController
    withState(viewModel) { state ->
        buildModels(state)
    }
}

fun <S : MvRxState, A : MvRxViewModel<S>> MvRxDialogFragment.simpleController(viewModel: A, buildModels: EpoxyController.(state: S) -> Unit) = MvRxEpoxyController {
    if (view == null || isRemoving) return@MvRxEpoxyController
    withState(viewModel) { state ->
        buildModels(state)
    }
}

