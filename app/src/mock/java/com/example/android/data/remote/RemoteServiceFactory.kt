package com.example.android.data.remote

import android.content.Context
import com.github.jasminb.jsonapi.ResourceConverter
import com.google.gson.Gson
import retrofit2.Retrofit

object RemoteServiceFactory {

    @Suppress("UNUSED_PARAMETER")
    fun create(retrofit: Retrofit, gson: Gson, jsonApiConverter: ResourceConverter, context: Context): RemoteService {
        return MockRemoteService(gson, jsonApiConverter, context)
    }
}