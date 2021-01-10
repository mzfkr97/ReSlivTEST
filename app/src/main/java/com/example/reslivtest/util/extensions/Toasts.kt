package com.example.reslivtest.util.extensions

import android.content.Context
import android.widget.Toast
import es.dmoral.toasty.Toasty


// default toast
fun Context.toast(message: String, duration: Int) {
    Toast.makeText(this, message, duration).show()
}

// toasty info
fun Context.showToastyInfo(snackMessage: String) {
    val toast = Toasty.info(this, snackMessage, Toast.LENGTH_SHORT)
    toast.show()
}

// toasty error
fun Context.showToastyError(snackMessage: String) {
    val toast = Toasty.error(this, snackMessage, Toast.LENGTH_LONG)
    toast.show()
}
