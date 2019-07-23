package com.example.android.utils

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.databinding.BindingAdapter
import androidx.annotation.DrawableRes
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.DrawableCompat
import com.example.android.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target

@SuppressLint("Range")
@BindingAdapter("imageUrl")
fun ImageView.setImageUrl(url: String?) {
    url?.let {
        Glide.with(context)
                .load(it)
                .apply(RequestOptions.overrideOf(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.DATA))
                .transition(DrawableTransitionOptions().crossFade())
                .into(this)
    }
}

@BindingAdapter("drawableRes")
fun ImageView.setDrawableFromResources(@DrawableRes res: Int?) {
    res?.let { setImageResource(it) }
}

@BindingAdapter("filterColor")
fun ImageView.setFilterColor(id: String?) {
    if (id == null) return

    val drawable = AppCompatResources.getDrawable(context, when (id) {
        "white" -> R.drawable.bg_filter_white
        "mix" -> R.drawable.bg_filter_mix
        else -> R.drawable.bg_filter_color
    })
    val color = when (id) {
        "white" -> null
        "mix" -> null
        "black" -> "#2A2A2A"
        "red" -> "#E15141"
        "magenta" -> "#6041B0"
        "brown" -> "#795548"
        "blue" -> "#4596EB"
        "orange" -> "#F19B38"
        "pink" -> "#EC6489"
        "grey" -> "#8B8B8B"
        "green" -> "#67AB5B"
        "yellow" -> "#FBE960"
        else -> null
    }

    setImageDrawable(drawable)
    if (color != null) {
        drawable?.let {
            it.mutate()
            DrawableCompat.setTint(it, Color.parseColor(color))
        }
    }
}