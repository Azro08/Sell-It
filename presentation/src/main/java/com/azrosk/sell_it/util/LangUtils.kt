package com.azrosk.sell_it.util

import android.content.Context
import java.util.Locale

class LangUtils(private val context: Context) {

    fun saveLang(lang: String) {
        val sharedPreferences =
            context.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(Constants.LANGUAGE_KEY, lang).apply()
    }

}

fun Context.setLocale(languageCode: String): Context {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)
    val config = resources.configuration
    config.setLocale(locale)
    config.setLayoutDirection(locale)
    return createConfigurationContext(config)
}