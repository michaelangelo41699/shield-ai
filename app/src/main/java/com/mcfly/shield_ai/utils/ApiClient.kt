package com.mcfly.shield_ai.utils

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:5000") // or your real backend URL
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}
