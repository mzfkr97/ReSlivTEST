package com.example.reslivtest.util.extensions

import android.annotation.SuppressLint
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.reslivtest.util.database.LocationResponse
import java.text.SimpleDateFormat
import java.util.*



@BindingAdapter("setTextService")
fun TextView.setTextService(locationResponse: LocationResponse?) {

    val hourPattern = "HH:mm:ss"
    val yearPattern = "dd MMMM"
    val dateSunrise = locationResponse?.sunrise?.let { unixToDate(it, hourPattern) }
    val dateSunset = locationResponse?.sunset?.let { unixToDate(it, hourPattern) }
    val date = locationResponse?.sunset?.let { unixToDate(it, yearPattern) }

    val serviceText = """
                Страна: ${locationResponse?.country}
                Скорость ветра:  ${locationResponse?.windSpeed.toString()}  м/с
                Рассвет: $dateSunrise
                Закат: $dateSunset
                Дата: $date
             
               """.trimIndent()
    locationResponse?.let {
        text = serviceText
    }
}


@SuppressLint("SimpleDateFormat")
fun unixToDate(timeStamp: Long, pattern: String) : String? {
    val time = Date(timeStamp * 1000)
    val sdf = SimpleDateFormat(pattern)
    return sdf.format(time)
}

