package com.example.android.ui.category

import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.airbnb.epoxy.EpoxyController
import com.airbnb.mvrx.fragmentViewModel
import com.example.android.R
import com.example.android.category
import com.example.android.data.model.Category
import com.example.android.ui.AppLifecycleObserver
import com.example.android.ui.MvRxFragment
import com.example.android.ui.simpleController
import com.example.android.utils.startActivity
import com.google.android.material.appbar.AppBarLayout
import javax.inject.Inject
import kotlin.math.ceil
import kotlin.math.cos

class CategoryListFragment : MvRxFragment() {
    @Inject
    lateinit var viewModelFactory: CategoryListViewModel.Factory
    private val viewModel: CategoryListViewModel by fragmentViewModel()

    override fun appLifecycleObserver(): AppLifecycleObserver = viewModel


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(recyclerView) {
            val manager = GridLayoutManager(context, 2).also { layoutManager = it }
            (itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false

            // Padding
            val padding = requireContext().resources.getDimensionPixelOffset(R.dimen.global_spacing_16)
            val categoryElevation = requireContext().resources.getDimensionPixelOffset(R.dimen.shadow_small)
            val radius = requireContext().resources.getDimensionPixelOffset(R.dimen.card_radius)
            val cos45 = cos(Math.toRadians(45.0))

            val hCategoryPadding = padding - ceil(categoryElevation.toDouble() + (1.0 - cos45) * radius.toDouble()).toInt()
            val hCategoryInnerPadding = padding - 2 * ceil(categoryElevation.toDouble() + (1.0 - cos45) * radius.toDouble()).toInt()
            val vCategoryInnerPadding = padding - 2 * ceil((categoryElevation * 1.5f).toDouble() + (1.0 - cos45) * radius.toDouble()).toInt()

            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                    if (parent.getChildAdapterPosition(view) == RecyclerView.NO_POSITION) return
                    val itemType = layoutManager?.getItemViewType(view)
                    val spanIndex = (view.layoutParams as GridLayoutManager.LayoutParams).spanIndex

                    if (itemType == R.layout.view_holder_category && spanIndex == 0) {
                        outRect.left = hCategoryPadding
                        outRect.right = hCategoryInnerPadding / 2
                        outRect.bottom = vCategoryInnerPadding / 2
                        outRect.top = vCategoryInnerPadding / 2
                    } else if (itemType == R.layout.view_holder_category && spanIndex == 1) {
                        outRect.left = hCategoryInnerPadding / 2
                        outRect.right = hCategoryPadding
                        outRect.bottom = vCategoryInnerPadding / 2
                        outRect.top = vCategoryInnerPadding / 2
                    }
                }
            })

            // Scrolling
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                val dimens = requireContext().resources.getDimensionPixelOffset(R.dimen.shadow_small).toFloat()
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    requireActivity().findViewById<AppBarLayout>(R.id.app_bar)?.let {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            val pos = manager.findFirstCompletelyVisibleItemPosition()
                            if (pos == 0 && it.elevation > 0) {
                                ViewCompat.setElevation(it, 0f)
                            } else if (pos != 0 && it.elevation < dimens) {
                                ViewCompat.setElevation(it, dimens)
                            }
                        }
                    }
                }
            })
        }
    }

    override fun epoxyController(): EpoxyController = simpleController(viewModel) { state ->
        state.categories.forEach { category ->
            category {
                id(category.id)
                image(category.image)
                name(category.name)
                listener(View.OnClickListener { onCategoryClick(category) })
                childCount(category.childCount)
                spanSizeOverride { _, _, _ -> 1 }
            }
        }
    }

    private fun onCategoryClick(category: Category) {
        startActivity<CategoryActivity>("id" to category.id, "name" to category.name, "childCount" to category.childCount, "depth" to category.depth)
    }
}