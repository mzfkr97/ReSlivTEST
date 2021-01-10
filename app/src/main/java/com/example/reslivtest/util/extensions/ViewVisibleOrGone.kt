package com.example.reslivtest.util.extensions

import android.view.View



fun checkViewVisibleOrGone (boolean: Boolean, view : View) {
    when (boolean) {
        true -> view.visibility = View.VISIBLE
        false -> view.visibility = View.GONE
    }
}
