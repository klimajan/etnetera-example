package com.example.android

object Config {
    const val LOGS = BuildConfig.LOGS
    const val DEV_ENVIRONMENT = BuildConfig.DEV_ENVIRONMENT
    @JvmField val APP_LOCALES: Array<String> = BuildConfig.APP_LOCALES

    const val GRANT_REFRESH = "refresh_token"

    // API clients information
    const val CLIENT_ID = BuildConfig.CLIENT_ID
    const val CLIENT_SECRET = BuildConfig.CLIENT_SECRET
    const val CLIENT_SCOPE = "user.identity mobile"

}
