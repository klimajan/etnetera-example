package com.example.android.ui

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.airbnb.mvrx.BaseMvRxActivity
import com.example.android.utility.LocaleContextWrapper
import dagger.android.AndroidInjection

abstract class MvRxSimpleActivity : BaseMvRxActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleContextWrapper.wrap(newBase))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
            else -> return false
        }
        return true
    }

    protected fun setupFragment(fragment: Fragment) {
        val manager = supportFragmentManager
        val current = manager.findFragmentById(com.example.android.R.id.container)
        if (current == null) {
            manager.beginTransaction().replace(com.example.android.R.id.container, fragment).commit()
        }
    }
}