/*
 * Copyright (C) 2018 Florian Dreier
 *
 * This file is part of MyTargets.
 *
 * MyTargets is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * MyTargets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package de.dreier.mytargets.features.training.environment

import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class WeatherService {

    private val mWebService: OpenWeatherMapWebService

    private interface OpenWeatherMapWebService {
        @GET("weather?units=metric")
        fun fetchCurrentWeather(@Query("lon") longitude: Double,
                                @Query("lat") latitude: Double,
                                @Query("APPID") appId: String): Call<CurrentWeather>
    }

    init {
        val client = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val original = chain.request()
                    val request = original.newBuilder()
                            .header("Accept", "application/json")
                            .method(original.method(), original.body())
                            .build()
                    chain.proceed(request)
                }
                .build()

        mWebService = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .baseUrl(BASE_URL)
                .build()
                .create(OpenWeatherMapWebService::class.java)
    }

    fun fetchCurrentWeather(longitude: Double, latitude: Double): Call<CurrentWeather> {
        return mWebService.fetchCurrentWeather(longitude, latitude, APP_ID)
    }

    companion object {
        private const val BASE_URL = "http://api.openweathermap.org/data/2.5/"
        private const val APP_ID = "180ecfe968fb986c95cf0f8da8620530"
    }
}
