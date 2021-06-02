package com.example.weather.viewmodel

import com.example.weather.data.model.WeatherCity
import com.example.weather.data.repository.Repository
import com.example.weather.utils.resource.Resource
import com.jakewharton.rxrelay3.BehaviorRelay
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.schedulers.Schedulers

class WeatherViewModel(private val repository: Repository): BaseViewModel() {
    private val resourceRecycler: BehaviorRelay<Resource<ArrayList<WeatherCity>>> = BehaviorRelay.create()
    private val resourceWeatherLocation: BehaviorRelay<Resource<WeatherCity>>
        = BehaviorRelay.createDefault(Resource(null))

    fun getResourceRecycler(): Observable<Resource<ArrayList<WeatherCity>>> = Observable.defer {
        if (resourceRecycler.value == null) {
            repository.getWeatherCities()
                .switchMap {
                    resourceRecycler.accept(Resource(it))
                    resourceRecycler
                }
        } else resourceRecycler
    }

    fun getResourceWeatherLocation(): Observable<Resource<WeatherCity>> = Observable.defer {
        if (resourceWeatherLocation.value.getData() == null
            && resourceWeatherLocation.value.getEvent() == null) {

            resourceWeatherLocation.mergeWith(
                repository.getCurrentLocationWeather()
                .toObservable()
                .switchMap {
                    resourceWeatherLocation.accept(Resource(it))
                    resourceWeatherLocation
                }
            )
        } else resourceWeatherLocation
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