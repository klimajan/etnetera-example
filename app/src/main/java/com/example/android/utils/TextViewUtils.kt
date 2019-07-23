package com.example.android.utils

import androidx.databinding.BindingAdapter
import android.os.Build
import android.text.Html
import android.widget.TextView

@BindingAdapter("android:text")
fun TextView.setText(text: Int?) {
    this.text = text?.toString() ?: ""
}

@Suppress("DEPRECATION")
@BindingAdapter("html")
fun TextView.setHtml(html: String?) {
    html?.let { chars ->
        val simpleText = chars.replace("<.?(iframe|font|a|img)(.|\\n)*?>".toRegex(), "").trim { it <= ' ' }.replace("\n", "<br/>")
        val tagHandler = Html.TagHandler { opening, tag, editable, _ ->
            if (tag == "ul" && !opening) editable.append("\n")
            if (tag == "li" && opening) editable.append("\nt•")
        }

        text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(simpleText, Html.FROM_HTML_MODE_LEGACY, null, tagHandler)
        } else {
            Html.fromHtml(simpleText, null, tagHandler)
        }
    }
}