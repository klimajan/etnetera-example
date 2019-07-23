package com.example.android.utility;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import java.util.Locale;


public class LocaleContextWrapper extends ContextWrapper {
    private static final String PREFERENCES_NAME = "com.example.android.language_preferences";
    private static final String APP_LANGUAGE = "app_language";
    public static final String DEFAULT_LANGUAGE = "cs";


    public LocaleContextWrapper(Context base) {
        super(base);
    }

    public static ContextWrapper wrap(Context context) {
        String language = getPersistedLanguage(context);
        return updateResources(context, language);
    }

    public static ContextWrapper wrap(Context context, String language) {
        persistLanguage(context, language);
        return updateResources(context, language);
    }

    private static ContextWrapper updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources res = context.getResources();
        Configuration config = res.getConfiguration();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale);
        } else config.locale = locale;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            context = context.createConfigurationContext(config);
        } else res.updateConfiguration(config, res.getDisplayMetrics());

        return new LocaleContextWrapper(context);
    }

    public static boolean hasPersistedLanguage(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return preferences.contains(APP_LANGUAGE);
    }

    public static String getPersistedLanguage(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return preferences.getString(APP_LANGUAGE, DEFAULT_LANGUAGE);
    }

    @SuppressLint("ApplySharedPref")
    public static void persistLanguage(Context context, String language) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        preferences.edit().putString(APP_LANGUAGE, language).commit();
    }
}
