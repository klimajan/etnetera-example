package com.example.android.di

import android.content.Context
import com.example.android.data.remote.RemoteServiceFactory
import com.example.android.Config
import com.example.android.data.LocalPrefs
import com.example.android.data.model.*
import com.example.android.data.remote.*
import com.example.android.data.remote.deserializer.FilterDeserializer
import com.example.android.utility.Logcat
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.jasminb.jsonapi.ResourceConverter
import com.github.jasminb.jsonapi.retrofit.JSONAPIConverterFactory
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import dagger.Module
import dagger.Provides
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
class RemoteModule {

    @Singleton
    @Provides
    fun provideRemoteService(retrofit: Retrofit, gson: Gson, jsonApi: ResourceConverter, context: Context): RemoteService {
        return RemoteServiceFactory.create(retrofit, gson, jsonApi, context)
    }

    @Singleton
    @Provides
    fun provideRetrofit(client: OkHttpClient, @Named("gson") gsonFactory: Converter.Factory): Retrofit {
        return Retrofit.Builder()
                .baseUrl("https://www.example.cz/")
                .client(client)
                .addConverterFactory(gsonFactory)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
    }

    @Singleton
    @Provides
    @Suppress("ConstantConditionIf")
    fun provideOkHttpClient(interceptor: Interceptor, authenticator: Authenticator): OkHttpClient {
        val builder = if (Config.DEV_ENVIRONMENT) {
            val logger = HttpLoggingInterceptor.Logger { Logcat.d(it) }
            val loggerInterceptor = HttpLoggingInterceptor(logger)
                    .apply { level = HttpLoggingInterceptor.Level.BASIC }

            OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(interceptor)
                    .authenticator(authenticator)
                    .addNetworkInterceptor(StethoInterceptor())
                    .addNetworkInterceptor(loggerInterceptor)
        } else {
            OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(interceptor)
                    .authenticator(authenticator)
        }

        return SocketFactory.wrap(builder).build()
    }

    @Singleton
    @Provides
    fun provideOauthAuthenticator(preferences: LocalPrefs): Authenticator = OauthAuthenticator(preferences)

    @Singleton
    @Provides
    fun provideServiceInterceptor(preferences: LocalPrefs): Interceptor = ServiceInterceptor(preferences)


    @Singleton
    @Provides
    @Named("jsonApi")
    fun provideJsonApiFactory(jsonApi: ResourceConverter): Converter.Factory {
        return JSONAPIConverterFactory(jsonApi)
    }

    @Singleton
    @Provides
    fun provideJsonApiConverter(): ResourceConverter {
        val objectMapper = jacksonObjectMapper().apply {
            propertyNamingStrategy = PropertyNamingStrategy.SNAKE_CASE
            dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        }

        return ResourceConverter(objectMapper, CategoryWithChildren::class.java)
                .apply { enableDeserializationOption(com.github.jasminb.jsonapi.DeserializationFeature.ALLOW_UNKNOWN_INCLUSIONS) }
    }


    @Singleton
    @Provides
    @Named("gson")
    fun createGsonFactory(gson: Gson): Converter.Factory {
        return GsonConverterFactory.create(gson)
    }

    @Singleton
    @Provides
    fun provideGson(filterDeserializer: FilterDeserializer): Gson {
        return GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .setDateFormat("EEE MMM d HH:mm:ss 'UTC'zzzzz yyyy")
                .registerTypeAdapter(object : TypeToken<Filter>() {}.type, filterDeserializer)
                .create()
    }

    @Singleton
    @Provides
    fun provideFilterDeserializer(): FilterDeserializer {
        return FilterDeserializer()
    }
}