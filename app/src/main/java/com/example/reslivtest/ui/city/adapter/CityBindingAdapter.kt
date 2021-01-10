package com.example.reslivtest.ui.city.adapter

import android.view.View
import androidx.databinding.BindingAdapter
import com.example.reslivtest.util.ColorMaker
import com.example.reslivtest.util.database.CityData


@BindingAdapter("setBackgroundColor")
fun View.setBackgroundColor(cityData: CityData?) {
    cityData?.let {
        setBackgroundColor(ColorMaker(context).getRandomMaterialColor("400"))
    }
}

