package com.example.android.data

import android.content.Context
import android.content.SharedPreferences
import com.example.android.data.model.Credentials
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalPrefs @Inject constructor(private val context: Context) {

    private val oauthPrefs: SharedPreferences by lazy {
        context.getSharedPreferences("com.example.android.oauth_preferences", Context.MODE_PRIVATE)
    }


    var credentials: Credentials
        get() {
            val accessToken = oauthPrefs.getString("access_token", "") ?: ""
            val tokenType = oauthPrefs.getString("token_type", "") ?: ""
            val refreshToken = oauthPrefs.getString("refresh_token", null)
            return Credentials(accessToken, tokenType, refreshToken)
        }
        set(value) {
            oauthPrefs.edit().apply {
                putString("access_token", value.accessToken)
                putString("token_type", value.tokenType)
                putBoolean("log_status", true)
                if (value.refreshToken != null) putString("refresh_token", value.refreshToken)
            }.apply()
        }
}