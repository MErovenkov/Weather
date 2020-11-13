package com.example.weather.utils

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.example.weather.MyApplication
import com.example.weather.di.component.ActivityComponent
import com.example.weather.model.WeatherCity
import com.example.weather.repository.dao.OrmLiteHelper
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * App extension
 */
fun AppCompatActivity.getActivityComponent(context: Context): ActivityComponent {
    return (application as MyApplication).appComponent.activityComponent().create(context)
}

/**
 * DB extension
 */
fun OrmLiteHelper.getWeatherCities(): ArrayList<WeatherCity> {
    return getWeatherCityDao().queryForAll() as ArrayList<WeatherCity>
}

fun OrmLiteHelper.getWeatherCityByName(nameCity: String): WeatherCity {
    return getWeatherCityDao().getWeatherCityByName(nameCity)
}

/**
 * StateFlow extension
 */
fun <T> MutableStateFlow<Resource<T>>.getData(): T? {
    return value.getData()
}