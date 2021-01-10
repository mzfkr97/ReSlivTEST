package com.example.reslivtest.util.repo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.reslivtest.ui.home.MainRepository
import com.example.reslivtest.ui.home.MainViewModel

class MainModelFactory(
    private val repository: MainRepository
):  ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewModel( repository ) as T
    }
}