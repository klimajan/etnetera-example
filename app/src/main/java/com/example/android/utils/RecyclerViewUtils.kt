package com.example.android.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.annotation.DimenRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.android.R

fun RecyclerView.decorItems(excludedIds: List<Int> = emptyList(), @DimenRes decorPaddingLeft: Int = R.dimen.global_spacing_0, @DimenRes decorPaddingRight: Int = R.dimen.global_spacing_0) {
    addItemDecoration(ListPaddingDecoration(context, resources.getDimension(decorPaddingLeft).toInt(), resources.getDimension(decorPaddingRight).toInt(), excludedIds))
}

class ListPaddingDecoration(context: Context, private val paddingLeft: Int, private val paddingRight: Int, private val excludeIds: List<Int> = emptyList()) : RecyclerView.ItemDecoration() {
    private var mDivider: Drawable? = null

    init {
        mDivider = ContextCompat.getDrawable(context, R.drawable.bg_divider)
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {

        val left = parent.paddingLeft + paddingLeft
        val right = parent.width - parent.paddingRight - paddingRight
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val isNotLastChild = i < childCount - 1
            val isIncluded = excludeIds.isEmpty() || !excludeIds.contains(parent.layoutManager?.getItemViewType(child))
            if (isIncluded && isNotLastChild) {
                val params = child.layoutParams as RecyclerView.LayoutParams
                val top = child.bottom + params.bottomMargin
                val bottom = top + (mDivider?.intrinsicHeight ?: 0)

                mDivider?.apply {
                    setBounds(left, top, right, bottom)
                    draw(c)
                }
            }
        }
    }
}