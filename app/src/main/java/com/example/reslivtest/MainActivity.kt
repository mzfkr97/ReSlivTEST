package com.example.reslivtest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.reslivtest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val actionMain = "com.example.reslivtest.ACTION_MAIN"
    private val actionCity = "com.example.reslivtest.ACTION_CITY"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_city,
                R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)

        shortCutsIntents()

    }

    private fun shortCutsIntents() {
        val action = intent.action
        action.let {
            when (it) {
                actionMain -> {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.navigation_home)
                }
                actionCity -> {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.navigation_city)
                }
            }
        }
    }

}