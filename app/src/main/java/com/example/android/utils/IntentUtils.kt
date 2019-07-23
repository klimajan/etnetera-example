package com.example.android.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import com.example.android.R
import com.example.android.utility.Logcat
import java.io.Serializable

inline fun <reified T : Activity> Activity.startActivity(vararg params: Pair<String, Any?>) {
    val intent = Intent(this, T::class.java).addParams(params)
    this.startActivity(intent)
}

inline fun <reified T : Activity> Activity.startActivity(apply: Intent.() -> Intent, vararg params: Pair<String, Any?>) {
    val intent = Intent(this, T::class.java).addParams(params).apply()
    this.startActivity(intent)
}

inline fun <reified T : Activity> Activity.startActivityForResult(requestCode: Int, vararg params: Pair<String, Any?>) {
    val intent = Intent(this, T::class.java).addParams(params)
    this.startActivityForResult(intent, requestCode)
}

inline fun <reified T : Activity> Activity.startActivityForResult(requestCode: Int, apply: Intent.() -> Intent, vararg params: Pair<String, Any?>) {
    val intent = Intent(this, T::class.java).addParams(params).apply()
    this.startActivityForResult(intent, requestCode)
}


inline fun <reified T : Activity> Fragment.startActivity(vararg params: Pair<String, Any?>) {
    val intent = Intent(activity, T::class.java).addParams(params)
    this.startActivity(intent)
}

inline fun <reified T : Activity> Fragment.startActivity(apply: Intent.() -> Intent, vararg params: Pair<String, Any?>) {
    val intent = Intent(activity, T::class.java).addParams(params).apply()
    this.startActivity(intent)
}

inline fun <reified T : Activity> Fragment.startActivityForResult(requestCode: Int, vararg params: Pair<String, Any?>) {
    val intent = Intent(activity, T::class.java).addParams(params)
    this.startActivityForResult(intent, requestCode)
}

inline fun <reified T : Activity> Fragment.startActivityForResult(requestCode: Int, apply: Intent.() -> Intent, vararg params: Pair<String, Any?>) {
    val intent = Intent(activity, T::class.java).addParams(params).apply()
    this.startActivityForResult(intent, requestCode)
}

inline fun <reified T : Activity> Activity.intentFor(vararg params: Pair<String, Any?>): Intent {
    return Intent(this, T::class.java).addParams(params)
}

inline fun <reified T : Activity> Activity.intentFor(apply: Intent.() -> Intent, vararg params: Pair<String, Any?>): Intent {
    return Intent(this, T::class.java).addParams(params).apply()
}

inline fun <reified T : Activity> Fragment.intentFor(vararg params: Pair<String, Any?>): Intent {
    return Intent(activity, T::class.java).addParams(params)
}

inline fun <reified T : Activity> Fragment.intentFor(apply: Intent.() -> Intent, vararg params: Pair<String, Any?>): Intent {
    return Intent(activity, T::class.java).addParams(params).apply()
}

inline fun <reified T : Activity> Context.intentFor(vararg params: Pair<String, Any?>): Intent {
    return Intent(this, T::class.java).addParams(params)
}

inline fun <reified T : Activity> Context.intentFor(apply: Intent.() -> Intent, vararg params: Pair<String, Any?>): Intent {
    return Intent(this, T::class.java).addParams(params).apply()
}


fun Fragment.makeCall(number: String) = requireActivity().makeCall(number)

fun Context.makeCall(number: String) {
    try {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
        ContextCompat.startActivity(this, Intent.createChooser(intent, "Some text"), null)
    } catch (e: Exception) {
        Logcat.e(e.toString())
    }
}

fun Fragment.sendEmail(email: String) = requireActivity().sendEmail(email)

fun Context.sendEmail(email: String) {
    try {
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email"))
        ContextCompat.startActivity(this, Intent.createChooser(intent, "Some text"), null)
    } catch (e: Exception) {
        Logcat.e(e.toString())
    }
}

fun Fragment.share(subject: String, text: String) = requireActivity().share(subject, text)

fun Context.share(subject: String, text: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_SUBJECT, subject);
        putExtra(Intent.EXTRA_TEXT, text);
        type = "text/plain";
    }

    try {
        ContextCompat.startActivity(this, Intent.createChooser(intent, "Some text"), null)
    } catch (e: Exception) {
        Logcat.e(e.toString())
    }
}

fun Intent.addParams(params: Array<out Pair<String, Any?>>): Intent {
    if (params.isNotEmpty()) {
        params.forEach {
            val value = it.second
            when (value) {
                null -> putExtra(it.first, null as Serializable?)
                is Int -> putExtra(it.first, value)
                is Long -> putExtra(it.first, value)
                is CharSequence -> putExtra(it.first, value)
                is String -> putExtra(it.first, value)
                is Float -> putExtra(it.first, value)
                is Double -> putExtra(it.first, value)
                is Char -> putExtra(it.first, value)
                is Short -> putExtra(it.first, value)
                is Boolean -> putExtra(it.first, value)
                is Serializable -> putExtra(it.first, value)
                is Bundle -> putExtra(it.first, value)
                is Parcelable -> putExtra(it.first, value)
                is Array<*> -> when {
                    value.isArrayOf<CharSequence>() -> putExtra(it.first, value)
                    value.isArrayOf<String>() -> putExtra(it.first, value)
                    value.isArrayOf<Parcelable>() -> putExtra(it.first, value)
                    else -> throw NoSuchElementException("extra ${it.first} has wrong type ${value.javaClass.name}")
                }
                is IntArray -> putExtra(it.first, value)
                is LongArray -> putExtra(it.first, value)
                is FloatArray -> putExtra(it.first, value)
                is DoubleArray -> putExtra(it.first, value)
                is CharArray -> putExtra(it.first, value)
                is ShortArray -> putExtra(it.first, value)
                is BooleanArray -> putExtra(it.first, value)
                else -> throw NoSuchElementException("extra ${it.first} has wrong type ${value.javaClass.name}")
            }
        }
    }
    return this
}

