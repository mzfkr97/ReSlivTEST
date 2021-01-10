package com.example.reslivtest.util.extensions

import android.annotation.SuppressLint
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.reslivtest.util.weather_response.WeatherCall
import java.text.SimpleDateFormat
import java.util.*



@BindingAdapter("setTextService")
fun TextView.setTextService(weatherCall: WeatherCall?) {

    val hourPattern = "HH:mm:ss"
    val yearPattern = "dd MMMM"
    val dateSunrise = weatherCall?.sys?.sunrise?.let { unixToDate(it, hourPattern) }
    val dateSunset = weatherCall?.sys?.sunset?.let { unixToDate(it, hourPattern) }
    val date = weatherCall?.sys?.sunset?.let { unixToDate(it, yearPattern) }

    val serviceText = """
                Страна: ${weatherCall?.sys?.country}
                Скорость ветра:  ${weatherCall?.wind?.speed.toString()}  м/с
                Рассвет: $dateSunrise
                Закат: $dateSunset
                Дата: $date
             
               """.trimIndent()
    weatherCall?.let {
        text = serviceText
    }
}


@SuppressLint("SimpleDateFormat")
fun unixToDate(timeStamp: Long, pattern: String) : String? {
    val time = Date(timeStamp * 1000)
    val sdf = SimpleDateFormat(pattern)
    return sdf.format(time)
}

