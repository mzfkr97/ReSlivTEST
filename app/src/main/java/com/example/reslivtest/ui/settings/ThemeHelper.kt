package com.example.reslivtest.ui.settings

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.example.reslivtest.BuildConfig
import com.example.reslivtest.util.Constants

class ThemeHelper {


    private var themeName: String? = null

    fun checkTheme(context: Context?) {

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        themeName = sharedPreferences.getString(Constants.THEME_KEY, Constants.THEME_LIGHT)
        if (BuildConfig.DEBUG && themeName == null) {
            error("Assertion failed")
        }
        when (themeName) {
            Constants.THEME_LIGHT -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            Constants.THEME_DARK -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            Constants.THEME_DEFAULT_FOLLOW_SYSTEM -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
                }
            }
        }
    }


    companion object {
        @JvmStatic
        fun applyTheme(themeName: String) {
            when (themeName) {
                Constants.THEME_LIGHT -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
                Constants.THEME_DARK -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
                Constants.THEME_DEFAULT_FOLLOW_SYSTEM -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
                    }
                }
            }
        }
    }


}
