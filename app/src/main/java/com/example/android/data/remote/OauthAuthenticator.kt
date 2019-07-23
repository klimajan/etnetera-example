package com.example.android.data.remote

import com.example.android.Config
import com.example.android.data.LocalPrefs
import com.example.android.data.model.Credentials
import com.example.android.utility.Logcat
import com.google.gson.Gson
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class OauthAuthenticator(private val preferences: LocalPrefs) : Authenticator {

    @Throws(IOException::class)
    override fun authenticate(route: Route?, response: Response): Request? {
        val credentials = preferences.credentials
        val refreshToken = credentials.refreshToken ?: return null

        val refreshResult = refreshToken("URL", refreshToken, Config.GRANT_REFRESH,
                Config.CLIENT_ID, Config.CLIENT_SECRET, Config.CLIENT_SCOPE)

        return if (refreshResult) {
            val newCredentials = preferences.credentials
            val accessToken = newCredentials.accessToken
            val tokenType = newCredentials.tokenType
            response.request().newBuilder()
                    .header("Authorization", "$tokenType $accessToken")
                    .build()
        } else null
    }

    @SuppressWarnings("all")
    @Throws(IOException::class)
    private fun refreshToken(url: String, refresh: String, grantType: String, clientId: String, client_secret: String, scope: String): Boolean {

        val refreshUrl = URL(url + "oauth/token")
        val urlParameters = "grant_type=$grantType&client_id=$clientId&client_secret=$client_secret&refresh_token=$refresh&scope=$scope"
        val urlConnection = (refreshUrl.openConnection() as HttpURLConnection)
                .apply {
                    requestMethod = "POST"
                    setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                    doInput = true
                    useCaches = false
                    doOutput = true
                }

        DataOutputStream(urlConnection.outputStream).run {
            writeBytes(urlParameters)
            flush()
            close()
        }

        val responseCode = urlConnection.responseCode
        val response = StringBuilder()
        with(BufferedReader(InputStreamReader(urlConnection.inputStream))) {
            forEachLine { response.append(it) }
            close()
        }

        val credentials = Gson().fromJson(response.toString(), Credentials::class.java)
        Logcat.d("Refresh token response code: $responseCode $response")

        return if (responseCode == 200) {
            preferences.credentials = credentials
            true
        } else {
            false
        }
    }
}