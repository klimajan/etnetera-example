package com.example.android.data.remote

import android.content.Context
import android.content.res.AssetManager
import com.example.android.data.model.CategoryWithChildren
import com.example.android.data.model.Filters
import com.github.jasminb.jsonapi.JSONAPIDocument
import com.github.jasminb.jsonapi.ResourceConverter
import com.google.gson.Gson
import io.reactivex.Single
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class MockRemoteService(private val gson: Gson, private val jsonApiConverter: ResourceConverter, private val context: Context) : RemoteService {
    private val mockAssetParser by lazy { MockAssetParser(context) }

    override fun getCategories(): Single<JSONAPIDocument<List<CategoryWithChildren>>> {
        val apiResponse = mockAssetParser.parseJsonApiResponseFile("categories.json")
        val response = jsonApiConverter.readDocumentCollection(apiResponse, CategoryWithChildren::class.java)
        return Single.just(response)
    }

    override fun getCategoryFilters(niceUrl: String, filters: String?): Single<Filters> {
        val jsonResponse = mockAssetParser.parseJsonResponseFile("filters.json")
        val response = gson.fromJson(jsonResponse, Filters::class.java)
        return Single.just(response)
    }

    inner class MockAssetParser(context: Context) {
        private val assetManager: AssetManager = context.assets

        fun parseJsonResponseFile(fileName: String): String {
            /* file in mock/assets/ */
            val stream = assetManager.open("response-mocks/$fileName")
            return parseStream(stream)
        }

        fun parseJsonApiResponseFile(fileName: String): InputStream {
            /* file in mock/assets/ */
            return assetManager.open("response-mocks/$fileName")
        }

        @Throws(IOException::class)
        private fun parseStream(stream: InputStream): String {
            val builder = StringBuilder()
            val inn = BufferedReader(InputStreamReader(stream, "UTF-8"))
            var line = inn.readLine()
            while (line != null) {
                builder.append(line)
                line = inn.readLine()
            }
            inn.close()
            return builder.toString()
        }
    }
}