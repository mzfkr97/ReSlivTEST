package com.example.reslivtest.ui.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.example.reslivtest.R
import com.example.reslivtest.util.Constants
import com.example.reslivtest.util.database.CityDatabase
import com.example.reslivtest.util.extensions.showToastyError
import com.example.reslivtest.util.extensions.showToastySuccess
import java.util.concurrent.Executors


class SettingsFragment :
    PreferenceFragmentCompat(),
        Preference.OnPreferenceClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener {
    companion object {
        private const val CLEAR_CACHE = "clear_cache"
    }

    private val executor = Executors.newSingleThreadExecutor()
    private lateinit var database : CityDatabase
    private var clearCache: Preference? = null
    private var refreshData: Preference? = null
    private var listTheme: Preference? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        database = CityDatabase(context?.applicationContext!!)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        clearCache = findPreference(CLEAR_CACHE)
        listTheme = findPreference(Constants.THEME_KEY)
        refreshData = findPreference(Constants.TIME_REFRESH_DATA)
        clearCache?.onPreferenceClickListener = this
        refreshData?.onPreferenceClickListener = this
        listTheme?.onPreferenceClickListener = this
    }

    override fun onPreferenceClick(preference: Preference?): Boolean {
        when(preference){
            clearCache -> {
                deleteDatabase()
            }
        }
        return true
    }


    private fun deleteDatabase() {
        executor.execute {
            database.cityDao().nukeTable()
        }
        activity?.showToastyError(getString(R.string.data_base_dropped))
    }

    override fun onSharedPreferenceChanged(preference: SharedPreferences?, key: String?) {
        when(key){
            Constants.TIME_REFRESH_DATA -> {
                activity?.showToastySuccess(getString(R.string.time_changed))
            }
            Constants.THEME_KEY -> {
                val themeName: String =
                    preference?.getString(Constants.THEME_KEY, Constants.THEME_LIGHT).toString()
                ThemeHelper.applyTheme(themeName)
                activity?.showToastySuccess(getString(R.string.theme_changed))
            }
        }
    }



}