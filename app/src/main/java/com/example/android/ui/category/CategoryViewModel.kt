package com.example.android.ui.category

import com.airbnb.mvrx.*
import com.example.android.R
import com.example.android.data.model.*
import com.example.android.data.repository.CategoryRepository
import com.example.android.ui.MvRxViewModel
import com.example.android.utility.LocaleContextWrapper
import com.example.android.utils.jointToStringNotEmpty
import com.example.android.utils.onSuccess
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject

data class CategoryState(val id: String,
                         val filters: List<Filter>? = null,
                         val params: String? = null,
                         val activeFilters: Int = 0,
                         val changedFilters: Boolean = false,
                         val sorting: List<Sorting>,
                         val sort: String? = null,
                         val subCategories: List<Category> = emptyList(),
                         val subCategoriesCount: Int,
                         val subcategoryDepth: Int,
                         val productsLoading: Boolean = true) : MvRxState

class CategoryViewModel @AssistedInject constructor(@Assisted private val initState: CategoryState, private val repo: CategoryRepository) : MvRxViewModel<CategoryState>(initState) {

    @AssistedInject.Factory
    interface Factory {
        fun create(initState: CategoryState): CategoryViewModel
    }

    companion object : MvRxViewModelFactory<CategoryViewModel, CategoryState> {
        @JvmStatic
        override fun create(viewModelContext: ViewModelContext, state: CategoryState): CategoryViewModel? {
            return (viewModelContext.activity as CategoryActivity).viewModelFactory.create(state)
        }

        override fun initialState(viewModelContext: ViewModelContext): CategoryState? {
            val categoryId = viewModelContext.activity.intent?.extras?.getString("id") ?: ""
            val subCategoriesCount = viewModelContext.activity.intent?.extras?.getInt("childCount")
                    ?: 0
            val categoryDepth = viewModelContext.activity.intent?.extras?.getInt("depth") ?: 0
            val context = LocaleContextWrapper.wrap(viewModelContext.activity.applicationContext)
            return CategoryState(id = categoryId,
                    sorting = listOf(
                            Sorting("default", context.getString(R.string.sort_default), true),
                            Sorting("price_min", context.getString(R.string.sort_price_min)),
                            Sorting("price_max", context.getString(R.string.sort_price_max)),
                            Sorting("new", context.getString(R.string.sort_new))),
                    subCategoriesCount = subCategoriesCount,
                    subcategoryDepth = categoryDepth + 1)
        }
    }

    init {
        getCategories()
        fetchFilters() // *
        selectSubscribe(CategoryState::params, CategoryState::sort) { _, _ ->
            fetchProducts()
            // In real app update filters when params or sort was changed
            // fetchFilters()
        }

    }

    /**
     * CATEGORIES
     */
    private fun getCategories() {
        withState { state ->
            repo.getCategoriesByParent(state.id)
                    .execute {
                        onSuccess(it) { categories ->
                            copy(subCategories = categories)
                        }
                    }
        }
    }

    /**
     * PRODUCTS
     */
    @Suppress("UNUSED_PARAMETER")
    private fun fetchProducts(first: Boolean = true) {
        // Download new products or next product handled by paging
    }

    fun fetchNextProducts() {
        fetchProducts(false)
    }


    /**
     * FILTERS
     */
    private fun fetchFilters(tempParams: Boolean = false) {
        withState { state ->
            val params = if (tempParams) getFilterParams(state.filters) else state.params
            repo.fetchCategoryFilters(state.id, params)
                    .execute {
                        onSuccess(it) { filterList ->
                            copy(filters = filterList)
                        }
                    }
        }
    }

    fun fetchFiltersWithTempParams() {
        // In real app update filters when some filter value is changed
        //fetchFilters(true)
    }

    private fun getFilterParams(filters: List<Filter>?): String? {
        return filters?.mapNotNull { filter -> filter.filterParams }?.jointToStringNotEmpty(separator = ", ", prefix = "{", postfix = "}")
    }

    fun applyFilters() {
        setState {
            val params = getFilterParams(filters)
            val activeFilters = filters?.filter { it.isActiveFilter }?.size ?: 0
            copy(params = params, activeFilters = activeFilters, changedFilters = false)
        }
    }

    fun setFilteredValue(name: String, value: String) {
        setState {
            copy(filters = filters?.map { filter ->
                if (filter.name == name) filter.setFilteredValue(value)
                else filter
            }, changedFilters = true)
        }
    }

    fun setSortingValue(value: String) {
        setState {
            copy(sorting = sorting.map { sorting ->
                if (sorting.value == value) sorting.copy(selected = true) else sorting.copy(selected = false)
            }, sort = value)
        }
    }

    fun updateStepper(name: String, min: Int, max: Int) {
        setState {
            copy(filters = filters?.map { filter ->
                if (filter.name == name && (filter is RangeFilter)) {
                    filter.setFilteredValue(filter.value.copy(min = min, max = max))
                } else filter
            }, changedFilters = true)
        }
    }

    fun updateStepperMin(name: String, increase: Boolean) {
        setState {
            copy(filters = filters?.map { filter ->
                if (filter.name == name && filter is StepperFilter) {
                    val value = filter.value
                    val settings = filter.settings
                    val updatedMin = if (increase) value.min + settings.stepSize else value.min - settings.stepSize
                    val newMin = when {
                        updatedMin < settings.min -> settings.min
                        updatedMin > settings.max -> settings.max
                        updatedMin > value.max -> value.max
                        else -> updatedMin
                    }
                    filter.setFilteredValue(value.copy(min = newMin))
                } else filter
            }, changedFilters = true)
        }
    }

    fun updateStepperMax(name: String, increase: Boolean) {
        setState {
            copy(filters = filters?.map { filter ->
                if (filter.name == name && filter is StepperFilter) {
                    val value = filter.value
                    val settings = filter.settings
                    val updatedMax = if (increase) value.max + settings.stepSize else value.max - settings.stepSize
                    val newMax = when {
                        updatedMax > settings.max -> settings.max
                        updatedMax < settings.min -> settings.min
                        updatedMax < value.min -> value.min
                        else -> updatedMax
                    }
                    filter.setFilteredValue(value.copy(max = newMax))
                } else filter
            }, changedFilters = true)
        }
    }

    fun resetFilter(name: String) {
        setState {
            copy(filters = filters?.map { filter ->
                if (filter.name == name) filter.resetFilter()
                else filter
            }, changedFilters = true)
        }
    }

    fun resetFilters() {
        setState {
            copy(filters = filters?.map { filter ->
                filter.resetFilter()
            }, changedFilters = true)
        }.also { fetchFiltersWithTempParams() }
    }
}