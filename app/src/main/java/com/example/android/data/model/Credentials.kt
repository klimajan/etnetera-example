package com.example.android.data.model

import com.google.gson.annotations.SerializedName

data class Credentials(@SerializedName("access_token") val accessToken: String,
                       @SerializedName("token_type") val tokenType: String,
                       @SerializedName("refresh_token") val refreshToken: String?)