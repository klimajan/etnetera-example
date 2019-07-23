package com.example.android.ui.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.example.android.*
import com.example.android.data.model.*
import com.example.android.ui.AppLifecycleObserver
import com.example.android.ui.MvRxFragment
import com.example.android.ui.category.CategoryState
import com.example.android.ui.category.CategoryViewModel
import com.example.android.ui.simpleController
import com.example.android.utils.decorItems
import com.example.android.utils.hide
import com.example.android.utils.show

class FilterFragment : MvRxFragment() {
    private val viewModel: CategoryViewModel by activityViewModel()
    private val filterName by lazy { arguments?.getString("name") }
    private lateinit var resetFilters: TextView
    override fun appLifecycleObserver(): AppLifecycleObserver = viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_filter_list, container, false).apply {
            recyclerView = findViewById<EpoxyRecyclerView>(R.id.recycler_view).apply {
                setController(epoxyController)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val title = withState(viewModel) { state -> state.filters?.firstOrNull { it.name == filterName }?.caption }
        view.findViewById<ImageView>(R.id.arrow)?.let {
            if (title == null) {
                it.hide()
            } else {
                it.setOnClickListener { activity?.onBackPressed() }
            }
        }
        view.findViewById<TextView>(R.id.title).text = title ?: getString(R.string.filter_button)
        resetFilters = view.findViewById<TextView>(R.id.reset_filter).also { reset ->
            filterName.let { name ->
                reset.text = if (name == null) getString(R.string.filter_reset_all) else getString(R.string.filter_reset_selected)
                reset.setOnClickListener { if (name == null) viewModel.resetFilters() else viewModel.resetFilter(name) }
            }
        }
        recyclerView.decorItems(decorPaddingLeft = R.dimen.global_spacing_72, decorPaddingRight = R.dimen.global_spacing_16, excludedIds = arrayListOf(R.layout.view_holder_filter))
        recyclerView.decorItems(decorPaddingLeft = R.dimen.global_spacing_16, excludedIds = arrayListOf(R.layout.view_holder_filter_single, R.layout.view_holder_filter_multi, R.layout.view_holder_filter_color))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        observeChanges()
    }

    private fun observeChanges() {
        viewModel.selectSubscribe(this, CategoryState::filters) { filters ->
            if (filterName == null) {
                if (filters?.any { it.isActiveFilter } == true) resetFilters.show() else resetFilters.hide()
            } else {
                if (filters?.firstOrNull { it.name == filterName }?.isActiveFilter == true) resetFilters.show() else resetFilters.hide()
            }
        }
    }

    override fun epoxyController(): EpoxyController = simpleController(viewModel) { state ->
        if (filterName == null) {
            state.filters?.forEach { filter ->
                filter {
                    id(filter.name)
                    name(filter.caption)
                    values(filter.filteredValue ?: getString(R.string.filter_all))
                    hasActiveFilters(filter.isActiveFilter)
                    listener(View.OnClickListener { onTopFilterClick(filter) })
                }
            }
        } else {
            when (val filter = state.filters?.firstOrNull { it.name == filterName }) {
                is OptionListFilter -> filter.options.forEach { option ->
                    val enabled = option.productCount ?: Int.MAX_VALUE > 0
                    filterSingle {
                        id(option.value)
                        name(option.label)
                        count(option.productCount ?: 0)
                        enabled(enabled)
                        selected(option.selected)
                        listener(if (enabled) View.OnClickListener { onSelectFilterClick(filter.name, option.value) } else null)
                    }
                }
                is TickListFilter -> filter.options.forEach { option ->
                    val enabled = option.productCount ?: Int.MAX_VALUE > 0
                    filterMulti {
                        id(option.value)
                        name(option.label)
                        count(option.productCount ?: 0)
                        enabled(enabled)
                        selected(option.selected)
                        listener(if (enabled) View.OnClickListener { onSelectFilterClick(filter.name, option.value) } else null)
                    }
                }
                is ColorFilter -> filter.options.forEach { option ->
                    filterColor {
                        id(option.value)
                        name(option.label)
                        count(option.productCount)
                        enabled(option.productCount > 0)
                        colorId(option.id)
                        selected(option.selected)
                        listener(if (option.productCount > 0) View.OnClickListener { onSelectFilterClick(filter.name, option.value) } else null)
                    }
                }
            }
        }
    }

    @Suppress("ReplaceSingleLineLet")
    private fun onTopFilterClick(filter: Filter) {
        val fragment = when (filter) {
            is StepperFilter -> FilterStepperFragment()
            is RangeFilter -> FilterStepperFragment()
            else -> FilterFragment()
        }.apply {
            arguments = Bundle().apply {
                putString("name", filter.name)
            }
        }

        fragmentManager?.let {
            it.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right)
                    .add(R.id.filter_container, fragment)
                    .addToBackStack(null)
                    .commit()
        }
    }

    private fun onSelectFilterClick(name: String, value: String) {
        viewModel.setFilteredValue(name, value)
    }
}