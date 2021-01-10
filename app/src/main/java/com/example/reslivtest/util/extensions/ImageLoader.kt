package com.example.reslivtest.util.extensions

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.reslivtest.R

@BindingAdapter("loadImageWithUrl")
fun ImageView.loadImageWithUrl(url: String?) {

    url.let {
        Glide.with(this)
                .load(url)
                .centerCrop()
                .placeholder(R.drawable.weather_placeholder)
                .into(this)
    }


}
