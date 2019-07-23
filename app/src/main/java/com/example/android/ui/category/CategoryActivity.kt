package com.example.android.ui.category

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.airbnb.mvrx.viewModel
import com.example.android.R
import com.example.android.ui.MvRxSimpleActivity
import com.example.android.ui.filter.FilterFragment
import com.example.android.ui.filter.SortingDialog
import com.example.android.utils.hide
import com.example.android.utils.show
import javax.inject.Inject

class CategoryActivity : MvRxSimpleActivity() {

    @Inject
    lateinit var viewModelFactory: CategoryViewModel.Factory
    private val viewModel: CategoryViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_detail)

        intent.extras?.let {
            val name = it.getString("name") ?: ""
            setupActionBar(name)
            setupFragment(CategoryFragment())
            setupFilterFragment()
        }
    }

    private fun setupActionBar(title: String) {
        findViewById<Toolbar>(R.id.toolbar)?.let { setSupportActionBar(it) }
        findViewById<TextView>(R.id.title)?.let { it.text = title }
        supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setupFilterFragment() {
        FilterFragment().let {
            supportFragmentManager.beginTransaction().replace(R.id.filter_container, it).commit()
        }

        findViewById<DrawerLayout>(R.id.drawer).addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}
            override fun onDrawerOpened(drawerView: View) {}
            override fun onDrawerStateChanged(newState: Int) {}
            override fun onDrawerClosed(drawerView: View) {
                with(supportFragmentManager) {
                    while (backStackEntryCount > 0) popBackStackImmediate()
                }
            }
        })

        findViewById<View>(R.id.sort_tab).setOnClickListener {
            openSorting()
        }

        findViewById<View>(R.id.filter_tab).setOnClickListener {
            openFilters()
        }

        viewModel.selectSubscribe(this, CategoryState::activeFilters) { activeFilters ->
            val label = findViewById<TextView>(R.id.filter_tab_subtitle)
            if (activeFilters > 0) {
                label.show()
                label.text = getString(R.string.filter_number_of_active, activeFilters)
            } else label.hide()
        }

        viewModel.selectSubscribe(this, CategoryState::changedFilters) { changed ->
            findViewById<View>(R.id.filter_button_layout).setBackgroundResource(if (changed) R.color.emerald else R.color.grey_4)
            findViewById<Button>(R.id.filter_button).setOnClickListener {
                if (changed) viewModel.applyFilters()
                closeDrawer()
            }
        }

        viewModel.selectSubscribe(this, CategoryState::sorting) { sorting ->
            val activeSorting = sorting.firstOrNull { it.selected }
            val label = findViewById<TextView>(R.id.sort_tab_subtitle)
            if (activeSorting != null && activeSorting.value != "default") {
                label.show()
                label.text = activeSorting.caption
            } else label.hide()
        }
    }

    private fun closeDrawer(): Boolean {
        with(findViewById<DrawerLayout>(R.id.drawer)) {
            return if (isDrawerOpen(GravityCompat.END)) {
                closeDrawer(GravityCompat.END)
                true
            } else false
        }
    }

    fun openFilters() {
        with(findViewById<DrawerLayout>(R.id.drawer)) {
            if (!isDrawerOpen(GravityCompat.END)) openDrawer(GravityCompat.END)
        }
    }

    private fun openSorting() {
        SortingDialog().show(supportFragmentManager, null)
    }

    override fun onBackPressed() {
        with(supportFragmentManager) {
            if (backStackEntryCount > 0) {
                viewModel.fetchFiltersWithTempParams()
                popBackStackImmediate()
                return
            }
        }

        if (closeDrawer()) return
        super.onBackPressed()
    }
}