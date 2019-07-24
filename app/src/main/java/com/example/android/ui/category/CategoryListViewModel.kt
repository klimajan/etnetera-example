package com.example.android.ui.category

import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.example.android.data.model.Category
import com.example.android.data.repository.CategoryRepository
import com.example.android.ui.MvRxViewModel
import com.example.android.utils.onSuccess
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject

data class CategoryListState(val categories: List<Category> = emptyList()) : MvRxState

class CategoryListViewModel @AssistedInject constructor(@Assisted initState: CategoryListState, private val categoryRepo: CategoryRepository) : MvRxViewModel<CategoryListState>(initState) {

    @AssistedInject.Factory
    interface Factory {
        fun create(initState: CategoryListState): CategoryListViewModel
    }

    companion object : MvRxViewModelFactory<CategoryListViewModel, CategoryListState> {
        @JvmStatic
        override fun create(viewModelContext: ViewModelContext, state: CategoryListState): CategoryListViewModel? {
            val parentFragment = (viewModelContext as FragmentViewModelContext).fragment
            return (parentFragment as CategoryListFragment).viewModelFactory.create(state)
        }
    }

    init {
        with(categoryRepo) {
            scheduleFetchCategories()
            getTopLevelCategories().execute {
                onSuccess(it) { categories ->
                    copy(categories = categories)
                }
            }
        }
    }
}