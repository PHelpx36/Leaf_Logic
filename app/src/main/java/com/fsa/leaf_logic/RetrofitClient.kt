package com.fsa.leaf_logic

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

object RetrofitClient {

    // Para Emulador Android
    private const val BASE_URL = "http://10.0.2.2:5246/" // Use HTTP para evitar problemas de SSL

    // Para Dispositivo Físico, substitua pelo IP local do seu computador, por exemplo:
    // private const val BASE_URL = "http://192.168.1.100:5000/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
