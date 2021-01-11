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
import com.example.reslivtest.util.extensions.showToastyInfo
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        database = CityDatabase(requireActivity())
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        clearCache = findPreference(CLEAR_CACHE)
        refreshData = findPreference(Constants.TIME_REFRESH_DATA)
        clearCache?.onPreferenceClickListener = this
        refreshData?.onPreferenceClickListener = this
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
            Constants.TIME_REFRESH_DATA -> activity?.showToastyInfo(getString(R.string.time_changed))
        }
    }
}