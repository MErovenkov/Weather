package com.example.weather.ui.theme

import android.view.View

interface IThemeManager {
    fun setTheme(view: View)
    fun getSavedTheme(): Int
}