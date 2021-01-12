package com.example.reslivtest.util.extensions

import android.content.Context
import android.widget.Toast
import es.dmoral.toasty.Toasty


fun Context.toast(message: String, duration: Int) {
    Toast.makeText(this, message, duration).show()
}

fun Context.showToastyInfo(snackMessage: String) {
    val toast = Toasty.info(this, snackMessage, Toast.LENGTH_SHORT)
    toast.show()
}

fun Context.showToastyError(snackMessage: String) {
    val toast = Toasty.error(this, snackMessage, Toast.LENGTH_LONG)
    toast.show()
}

fun Context.showToastySuccess(snackMessage: String) {
    val toast = Toasty.success(this, snackMessage, Toast.LENGTH_LONG)
    toast.show()
}
