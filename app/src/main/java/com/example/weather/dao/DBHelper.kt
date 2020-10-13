package com.example.weather.dao

import android.content.Context
import com.j256.ormlite.android.apptools.OpenHelperManager

object DBHelper {
    private var databaseHelper: OrmLiteHelper? = null

    fun getDB(): OrmLiteHelper {
        return databaseHelper!!
    }

    fun setContext(context: Context) {
        databaseHelper = OpenHelperManager.getHelper(context, OrmLiteHelper::class.java)
    }

    fun releaseDB() {
        OpenHelperManager.releaseHelper()
        databaseHelper = null
    }
}