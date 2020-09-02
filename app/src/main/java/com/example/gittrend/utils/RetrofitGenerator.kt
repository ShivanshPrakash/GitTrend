package com.example.gittrend.utils

import com.example.gittrend.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by Shivansh ON 02/09/20.
 */
object RetrofitGenerator {
    private val interceptor = HttpLoggingInterceptor()
    private var timeout: Long = 2000

    init {
        if (BuildConfig.DEBUG) {
            interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC)
            timeout = 10000
        } else {
            interceptor.setLevel(HttpLoggingInterceptor.Level.NONE)
        }
    }

    private val client = OkHttpClient.Builder()
        .readTimeout(timeout, TimeUnit.SECONDS)
        .writeTimeout(timeout, TimeUnit.SECONDS)
        .addInterceptor(interceptor).build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://ghapi.huchen.dev/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    fun <T> create(service: T): Any = retrofit.create(service as Class<*>)
}