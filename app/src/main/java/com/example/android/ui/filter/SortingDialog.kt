package com.example.android.ui.filter

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.SimpleItemAnimator
import com.airbnb.mvrx.activityViewModel
import com.example.android.R
import com.example.android.filterSingle
import com.example.android.ui.MvRxDialogFragment
import com.example.android.ui.category.CategoryViewModel
import com.example.android.ui.simpleController
import com.example.android.utils.decorItems

class SortingDialog : MvRxDialogFragment() {
    private val viewModel: CategoryViewModel by activityViewModel()
    override fun appLifecycleObserver() = viewModel
    override fun title(): String = requireContext().getString(R.string.sort_button)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(recyclerView) {
            (itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
            decorItems(decorPaddingLeft = R.dimen.global_spacing_16, decorPaddingRight = R.dimen.global_spacing_16)
        }
    }

    override fun epoxyController() = simpleController(viewModel) { state ->
        state.sorting.forEach { sorting ->
            filterSingle {
                id(sorting.value)
                name(sorting.caption)
                enabled(true)
                selected(sorting.selected)
                listener(View.OnClickListener {
                    viewModel.setSortingValue(sorting.value)
                    this@SortingDialog.dismiss()
                })
            }
        }
    }
}