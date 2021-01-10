package com.example.reslivtest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.reslivtest.databinding.ActivityMainBinding
import com.example.reslivtest.ui.city.CityModelFactory
import com.example.reslivtest.ui.city.CityRepository
import com.example.reslivtest.ui.city.CityViewModel
import com.example.reslivtest.ui.home.MainRepository
import com.example.reslivtest.ui.home.MainViewModel
import com.example.reslivtest.util.database.CityDatabase
import com.example.reslivtest.util.repo.MainModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var viewModel : CityViewModel
    lateinit var viewModelCity: MainViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val mainRepository = MainRepository(CityDatabase(this))
        val mainViewModelFactory = MainModelFactory(mainRepository)
        viewModelCity = ViewModelProvider(this, mainViewModelFactory).get(MainViewModel::class.java)

        val repository = CityRepository(CityDatabase(this))
        val factory = CityModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(CityViewModel::class.java)



        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications))
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)
    }

}