package com.example.android.ui.category

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.airbnb.epoxy.EpoxyController
import com.airbnb.mvrx.activityViewModel
import com.example.android.*
import com.example.android.data.model.Category
import com.example.android.ui.AppLifecycleObserver
import com.example.android.ui.MvRxFragment
import com.example.android.ui.simpleController
import com.example.android.utils.startActivity

class CategoryFragment : MvRxFragment() {
    private val viewModel: CategoryViewModel by activityViewModel()
    override fun appLifecycleObserver(): AppLifecycleObserver = viewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(recyclerView) {
            val padding = requireContext().resources.getDimensionPixelOffset(R.dimen.global_spacing_16)
            setPadding(padding, padding, padding, padding)
            layoutManager = GridLayoutManager(context, 2)
            (itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
        }
    }

    override fun epoxyController(): EpoxyController = simpleController(viewModel) { state ->

        for (index in 0 until state.subCategoriesCount) {
            val category = state.subCategories.getOrNull(index)
            val depth = state.subcategoryDepth

            if (depth < 2) {
                categoryLevelTwo {
                    id("category_$index")
                    category(category)
                    listener(View.OnClickListener { onCategoryClick(category) })
                    spanSizeOverride { _, _, _ -> 2 }
                }
            } else if (depth >= 2) {
                categoryLevelOther {
                    id("category_$index")
                    category(category)
                    listener(View.OnClickListener { onCategoryClick(category) })
                    spanSizeOverride { _, _, _ -> 1 }
                }

                state.subCategoriesCount.let { size ->
                    if (index == size - 1 && size % 2 > 0) {
                        emptyLayout {
                            id("empty_category_$index")
                            spanSizeOverride { _, _, _ -> 1 }
                        }
                    }
                }
            }

        }

        for (index in 1..4) {
            product {
                id(index)
                animated(state.productsLoading)
                spanSizeOverride { _, _, _ -> 1 }
            }
        }

    }

    private fun onCategoryClick(category: Category?) {
        if (category == null) return
        if (category.id.isNotEmpty()) {
            startActivity<CategoryActivity>(
                    "id" to category.id,
                    "name" to category.name,
                    "childCount" to category.childCount,
                    "depth" to category.depth)
        }
    }
}