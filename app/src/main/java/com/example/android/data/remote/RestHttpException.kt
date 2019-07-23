package com.example.android.data.remote

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.jasminb.jsonapi.models.errors.Errors
import retrofit2.HttpException
import retrofit2.Response


class RestHttpException(response: Response<*>) : HttpException(response) {

    val responseErrors: Errors = parseError(response)

    // this method can be called just once because it reads data from stream
    private fun parseError(response: Response<*>): Errors {
        val errorBody = response.errorBody() ?: return Errors()
        return try {
            val objectMapper = ObjectMapper()
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            objectMapper.readValue(errorBody.bytes(), Errors::class.java) ?: Errors()
        } catch (e: Exception) {
            Errors()
        }
    }
}