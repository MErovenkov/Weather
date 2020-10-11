package com.example.weather.view.toast

import android.content.Context
import android.widget.Toast

object ShowToast {
    fun getToast(context: Context, info: String) {
        val toast = Toast.makeText(context, info, Toast.LENGTH_SHORT)
        toast.show()
    }
}