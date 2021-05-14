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

    fun getResourceRecycler(): Observable<Resource<ArrayList<WeatherCity>>> {
        return repository.getWeatherCities()
            .switchMap {
                if (resourceRecycler.value == null) {
                    resourceRecycler.accept(Resource(it))
                }
               resourceRecycler
            }
    }

    fun getResourceWeatherLocation(): Observable<Resource<WeatherCity>> {
        return resourceWeatherLocation.mergeWith (
            repository.getCurrentLocationWeather()
                .toObservable()
                .switchMap {
                    if (resourceWeatherLocation.value.getData() == null) {
                        resourceWeatherLocation.accept(Resource(it))
                    }
                    resourceWeatherLocation
                }
        )
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