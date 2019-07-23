package com.example.android.data.remote

import com.example.android.data.LocalPrefs
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class ServiceInterceptor(private val preferences: LocalPrefs) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val credentials = preferences.credentials
        val accessToken = credentials.accessToken
        val tokenType = credentials.tokenType
        val builder = request.newBuilder()
                .header("Accept", "application/json")
                .header("Accept-Charset", "utf-8")
                .header("Content-Type", "application/json")

        if (request.header("Authorization") == null) {
            builder.header("Authorization", "$tokenType $accessToken")
        }

        return chain.proceed(builder.build())
    }
}