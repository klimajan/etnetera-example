package com.example.android.utils

import android.content.Context
import com.airbnb.epoxy.*
import com.airbnb.mvrx.*

fun <T> Async<T>?.handle(onSuccess: (success: T) -> Unit = {}, onLoading: () -> Unit = {}, onUninitialized: () -> Unit = {}, onFail: () -> Unit = {}) {
    when (this) {
        is Success -> onSuccess(this.invoke())
        is Loading -> onLoading()
        is Uninitialized -> onUninitialized()
        is Fail -> onFail()
    }
}

fun <T> Async<T>?.onSuccess(onSuccess: (success: T) -> Unit) {
    this.handle(onSuccess = onSuccess)
}

fun <T> Async<T>?.onLoading(onLoading: () -> Unit) {
    this.handle(onLoading = onLoading)
}

fun <T> Async<T>?.onFail(onFail: () -> Unit) {
    this.handle(onFail = onFail)
}

fun <T> Async<T>?.onUninitialized(onUninitialized: () -> Unit) {
    this.handle(onUninitialized = onUninitialized)
}

fun <T> T.toSuccess(): Success<T> {
    return Success(this)
}

fun <T, S: MvRxState> S.onSuccess(asyncVal: Async<T>, stateReducer: S.(T) -> S): S {
    return if (asyncVal is Success && asyncVal() != null) {
        stateReducer(asyncVal())
    } else this
}


/** For use in the buildModels method of EpoxyController. A shortcut for creating a Carousel model, initializing it, and adding it to the controller.
 *
 */
inline fun EpoxyController.carousel(modelInitializer: CarouselModelBuilder.() -> Unit) {
    CarouselModel_().apply {
        modelInitializer()
    }.addTo(this)
}

/** Add models to a CarouselModel_ by transforming a list of items into EpoxyModels.
 *
 * @param items The items to transform to models
 * @param modelBuilder A function that take an item and returns a new EpoxyModel for that item.
 */
inline fun <T> CarouselModelBuilder.withModelsFrom(items: List<T>, modelBuilder: (Int, T) -> EpoxyModel<*>) {
    models(items.mapIndexed { index, t -> modelBuilder(index, t) })
}

inline fun <T> LeftConstrainedCarouselModelBuilder.withModelsFrom(items: List<T>, modelBuilder: (Int, T) -> EpoxyModel<*>) {
    models(items.mapIndexed { index, t -> modelBuilder(index, t) })
}

/**
 * OTHER CAROUSELS
 */

@ModelView(saveViewState = true, autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class LeftConstrainedCarousel(c: Context) : Carousel(c) {

    override fun getSnapHelperFactory(): SnapHelperFactory? {
        return null
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        adapter?.registerAdapterDataObserver(object : androidx.recyclerview.widget.RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (positionStart == 0) scrollToPosition(0)
            }

            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount)
                if (toPosition == 0) scrollToPosition(0)
            }
        })
    }
}