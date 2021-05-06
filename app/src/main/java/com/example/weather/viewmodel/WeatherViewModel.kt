package com.example.weather.viewmodel

import com.example.weather.data.model.WeatherCity
import com.example.weather.data.repository.Repository
import com.example.weather.utils.resource.Resource
import com.jakewharton.rxrelay3.BehaviorRelay
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlin.collections.ArrayList

class WeatherViewModel(private val repository: Repository): BaseViewModel() {
    val resourceRecycler: BehaviorRelay<Resource<ArrayList<WeatherCity>>> by lazy {
        BehaviorRelay.createDefault(Resource(repository.getWeatherCities()))
    }

    val resourceWeatherLocation: BehaviorRelay<Resource<WeatherCity>> by lazy {
        BehaviorRelay.createDefault(Resource(repository.getCurrentLocationWeather()))
    }

    fun createWeatherData(nameCity: String) {
        compositeDisposable.add(repository.createWeatherCity(nameCity)
            .subscribeOn(Schedulers.io())
            .subscribe(Consumer { addWeatherData(it) })
        )
    }

    private fun addWeatherData(resourceWeatherCity: Resource<WeatherCity>) {
        val tmp: ArrayList<WeatherCity> = ArrayList(resourceRecycler.value.getData())
        resourceWeatherCity.getData()?.let { tmp.add(it) }
        resourceRecycler.accept(
            resourceWeatherCity.getEvent()?.getStatusIfNotHandled()?.let { Resource(it, tmp)}!!
        )
    }

    fun updateWeatherCities() {
        compositeDisposable.add(repository.updateWeatherCities()
            .subscribeOn(Schedulers.io())
            .subscribe(Consumer { resourceRecycler.accept(it) })
        )
    }

    fun deleteWeatherCity(weatherCity: WeatherCity) {
        repository.deletedWeatherCity(weatherCity)
    }

    /**
     *  Current Location
     * */
    fun createWeatherCurrentLocation(coordinateLat: Double, coordinateLon: Double) {
        compositeDisposable.add(repository.createWeatherCurrentLocation(coordinateLat, coordinateLon)
            .subscribeOn(Schedulers.io())
            .subscribe(Consumer { resourceWeatherLocation.accept(it) })
        )
    }

    fun createWeatherCurrentLocation(nameCity: String) {
        compositeDisposable.add(repository.createWeatherCurrentLocation(nameCity)
            .subscribeOn(Schedulers.io())
            .subscribe(Consumer { resourceWeatherLocation.accept(it) })
        )
    }
}