package com.example.android.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.android.R
import com.example.android.ui.category.CategoryListFragment


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupFragment(CategoryListFragment())
    }

    private fun setupFragment(fragment: Fragment) {
        val manager = supportFragmentManager
        val current = manager.findFragmentById(R.id.container)
        if (current == null) {
            manager.beginTransaction().replace(R.id.container, fragment).commit()
        }
    }
}
