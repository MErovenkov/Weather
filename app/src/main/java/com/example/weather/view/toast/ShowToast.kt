package com.example.weather.view.toast

import android.content.Context
import android.widget.Toast

object ShowToast {
    private var mContext: Context? = null

    fun getToast(info: String) {
        val toast = Toast.makeText(mContext, info, Toast.LENGTH_SHORT)
        toast.show()
    }

    fun setContext(context: Context) {
        this.mContext = context.applicationContext
    }
}