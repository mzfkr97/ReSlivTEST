package com.example.reslivtest.util.application

import android.app.Application
import com.example.reslivtest.util.database.CityDatabase

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        CityDatabase.invoke(this)

    }
}