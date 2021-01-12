package com.example.reslivtest.util

import android.content.Context
import androidx.preference.PreferenceManager

object PreferencesHelper {

    fun workerIsEnable(context: Context): String {
        return PreferenceManager
            .getDefaultSharedPreferences(context)
            .getString(Constants.TIME_REFRESH_DATA, "no").toString()
    }

    fun nightModeEnable(context: Context): Boolean {
        val mode = PreferenceManager
            .getDefaultSharedPreferences(context)
            .getString(Constants.THEME_KEY, Constants.THEME_LIGHT)
        return when (mode) {
            Constants.THEME_DARK -> {
                true
            }
            else -> false

        }

    }

}