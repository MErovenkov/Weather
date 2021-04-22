package com.example.weather.viewmodel

import com.example.weather.data.model.WeatherCity
import com.example.weather.data.repository.Repository
import com.example.weather.utils.resource.Resource
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlin.collections.ArrayList

class WeatherViewModel(private val repository: Repository): BaseViewModel() {
    val resourceRecycler: BehaviorSubject<Resource<ArrayList<WeatherCity>>> = BehaviorSubject.create()
    val resourceWeatherLocation: BehaviorSubject<Resource<WeatherCity>> = BehaviorSubject.create()

    fun getWeatherCities() {
        resourceRecycler.onNext(Resource(repository.getWeatherCities()))
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
        resourceRecycler.onNext(
            resourceWeatherCity.getEvent()?.getStatusIfNotHandled()?.let { Resource(it, tmp)}!!
        )
    }

    fun updateWeatherCities() {
        compositeDisposable.add(repository.updateWeatherCities()
            .subscribeOn(Schedulers.io())
            .subscribe(Consumer { resourceRecycler.onNext(it) })
        )
    }

    fun deleteWeatherCity(weatherCity: WeatherCity) {
        repository.deletedWeatherCity(weatherCity)
    }

    /**
     *  Current Location
     * */

    fun getCurrentLocation() {
        resourceWeatherLocation.onNext(Resource(repository.getCurrentLocationWeather()))
    }

    fun createWeatherCurrentLocation(coordinateLat: Double, coordinateLon: Double) {
        compositeDisposable.add(repository.createWeatherCurrentLocation(coordinateLat, coordinateLon)
            .subscribeOn(Schedulers.io())
            .subscribe(Consumer { resourceWeatherLocation.onNext(it) })
        )
    }

    fun createWeatherCurrentLocation(nameCity: String) {
        compositeDisposable.add(repository.createWeatherCurrentLocation(nameCity)
            .subscribeOn(Schedulers.io())
            .subscribe(Consumer { resourceWeatherLocation.onNext(it) })
        )
    }
}