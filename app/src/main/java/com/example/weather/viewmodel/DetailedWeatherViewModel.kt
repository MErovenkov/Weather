package com.example.weather.viewmodel

import com.example.weather.data.repository.Repository
import com.example.weather.data.model.WeatherCity
import com.example.weather.utils.resource.Resource
import com.jakewharton.rxrelay3.BehaviorRelay
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.schedulers.Schedulers

class DetailedWeatherViewModel(private val repository: Repository): BaseViewModel() {
    val resourceDetailedWeather: BehaviorRelay<Resource<WeatherCity>> = BehaviorRelay.create()

    fun initResource(nameCity: String, isCurrentLocation: Boolean) {
        resourceDetailedWeather.accept(Resource(
            when(isCurrentLocation) {
                true -> repository.getCurrentLocationWeather()
                false -> repository.getWeatherCityByName(nameCity)
            }
        ))
    }

    fun initResourceByDeepLinkData(nameCity: String) {
        compositeDisposable.add(repository.getWeatherCityByDeepLinkData(nameCity)
            .subscribeOn(Schedulers.io())
            .subscribe(Consumer { resourceDetailedWeather.accept(it) })
        )
    }

    fun updateWeatherCity() {
        compositeDisposable.add(resourceDetailedWeather.value.getData()?.let { weatherCity ->
            repository.updateWeatherCity(weatherCity)
                .subscribeOn(Schedulers.io())
                .subscribe(Consumer { resourceDetailedWeather.accept(it) })
        })
    }
}