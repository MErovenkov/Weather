package com.merovenkov.weather.data.repository.api.interfaces

import com.merovenkov.weather.data.dto.WeatherCurrentDto
import com.merovenkov.weather.data.dto.WeatherFutureDto
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface IWeatherApi {
    @GET("/data/2.5/weather?units=metric&lang=ru")
    fun getWeatherCurrent(
        @Query("q") name: String,
        @Query("appid") appid: String): Call<WeatherCurrentDto>

    @GET("/data/2.5/weather?units=metric&lang=ru")
    fun getWeatherCurrentByCoordinate(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("appid") appid: String): Call<WeatherCurrentDto>

    /**
     *  @return Call<WeatherFutureDto> which is different from
     *  @see IWeatherApi.getWeatherCurrentByCoordinate(Sting, String, String): Call<WeatherCurrentDto>
     *  that return data contains only the name of the time zone, no name of the place
     * */
    @GET("/data/2.5/onecall?&exclude=minutely,hourly,current&units=metric&lang=ru")
    fun getWeatherFuture(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("appid") appid: String): Call<WeatherFutureDto>

    @GET("https://tile.openweathermap.org/map/{layer}/{z}/{x}/{y}.png")
    fun getPrecipitationBitmap(
        @Path("layer") layer: String,
        @Path("z") z: Int,
        @Path("x") x: Int,
        @Path("y") y: Int,
        @Query("appid") appid: String): Call<ResponseBody>
}