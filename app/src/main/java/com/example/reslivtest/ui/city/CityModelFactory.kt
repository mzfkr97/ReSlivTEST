package com.example.reslivtest.ui.city

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CityModelFactory(
    private val repository: CityRepository
    ):  ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return CityViewModel(repository)  as T
        }



}
