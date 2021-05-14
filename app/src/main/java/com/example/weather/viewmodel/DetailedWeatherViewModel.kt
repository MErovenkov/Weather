package com.example.weather.viewmodel

import com.example.weather.data.repository.Repository
import com.example.weather.data.model.WeatherCity
import com.example.weather.utils.resource.Resource
import com.jakewharton.rxrelay3.BehaviorRelay
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.schedulers.Schedulers

class DetailedWeatherViewModel(private val repository: Repository): BaseViewModel() {
    private val resourceDetailedWeather: BehaviorRelay<Resource<WeatherCity>> = BehaviorRelay.create()

    fun getResourceDetailedWeather(): Observable<Resource<WeatherCity>> = resourceDetailedWeather

    fun initResource(nameCity: String, isCurrentLocation: Boolean) {
        when(isCurrentLocation) {
            true -> {
                compositeDisposable.add(repository.getCurrentLocationWeather()
                    .subscribeOn(Schedulers.io())
                    .subscribe { resourceDetailedWeather.accept(Resource(it)) }
                )
            }
            false -> {
                compositeDisposable.add(repository.getWeatherCityByName(nameCity)
                    .subscribeOn(Schedulers.io())
                    .subscribe { resourceDetailedWeather.accept(Resource(it)) }
                )
            }
        }
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