package com.example.android.utils


fun <T> List<T>.filterMap(predicate: (T) -> Boolean, transform: (T) -> T): List<T> {
    return this.map { if (predicate(it)) transform(it) else it }
}

fun <T> List<T>.jointToStringNotEmpty(separator: CharSequence = ", ", prefix: CharSequence = "", postfix: CharSequence = ""): String? {
    return if (isEmpty()) null else joinToString(separator = separator, prefix = prefix, postfix = postfix)
}