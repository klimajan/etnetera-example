package com.example.android.data.remote


import com.example.android.data.model.CategoryWithChildren
import com.example.android.data.model.Filters
import com.github.jasminb.jsonapi.JSONAPIDocument
import io.reactivex.Single
import retrofit2.http.*

interface RemoteService {
    /**
     * =======================================
     * CATEGORIES
     * =======================================
     */

    @GET("api/mobile/v0/category")
    fun getCategories(): Single<JSONAPIDocument<List<CategoryWithChildren>>>

    @GET("api/mobile/v0/ugly/category-filters/{nice_url}")
    fun getCategoryFilters(@Path("nice_url") niceUrl: String, @Query("uglyfilters") filters: String?): Single<Filters>
}