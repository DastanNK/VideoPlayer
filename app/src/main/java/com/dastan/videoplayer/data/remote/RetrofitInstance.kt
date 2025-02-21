package com.dastan.videoplayer.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.dastan.videoplayer.BuildConfig


object RetrofitInstance {
    private const val BASE_URL = BuildConfig.BASE_URL

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: Api by lazy {
        retrofit.create(Api::class.java)
    }
}