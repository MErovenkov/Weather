package com.example.weather.viewmodel

import com.example.weather.data.repository.Repository
import com.example.weather.data.model.WeatherCity
import com.example.weather.utils.resource.Resource
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject

class DetailedWeatherViewModel(private val repository: Repository): BaseViewModel() {
    val resourceDetailedWeather: BehaviorSubject<Resource<WeatherCity>> = BehaviorSubject.create()

    fun initResource(nameCity: String, isCurrentLocation: Boolean) {
        resourceDetailedWeather.onNext(Resource(
            when(isCurrentLocation) {
                true -> repository.getCurrentLocationWeather()
                false -> repository.getWeatherCityByName(nameCity)
            }
        ))
    }

    fun initResourceByDeepLinkData(nameCity: String) {
        compositeDisposable.add(repository.getWeatherCityByDeepLinkData(nameCity)
            .subscribeOn(Schedulers.io())
            .subscribe(Consumer { resourceDetailedWeather.onNext(it) })
        )
    }

    fun updateWeatherCity() {
        compositeDisposable.add(resourceDetailedWeather.value.getData()?.let { weatherCity ->
            repository.updateWeatherCity(weatherCity)
                .subscribeOn(Schedulers.io())
                .subscribe(Consumer { resourceDetailedWeather.onNext(it) })
        })
    }
}