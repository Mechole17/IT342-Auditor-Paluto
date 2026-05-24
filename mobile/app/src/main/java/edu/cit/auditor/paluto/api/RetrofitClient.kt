package edu.cit.auditor.paluto.api

import android.content.Context
import edu.cit.auditor.paluto.PalutoApp
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://192.168.254.109:8080/"

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val context = PalutoApp.getContext()
                val sharedPref = context.getSharedPreferences("PalutoPrefs", Context.MODE_PRIVATE)
                val token = sharedPref.getString("JWT_TOKEN", "")

                val newRequest = if (!token.isNullOrEmpty()) {
                    chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                } else {
                    chain.request()
                }
                chain.proceed(newRequest)
            }
            .build()
    }

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
