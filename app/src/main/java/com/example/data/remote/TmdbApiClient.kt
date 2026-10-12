package com.example.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class TmdbApiClient(
    private val apiKeyProvider: () -> String
) {
    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val originalUrl = originalRequest.url
        val key = apiKeyProvider().trim()

        if (key.isBlank() || key == "YOUR_TMDB_API_KEY") {
            // No valid key supplied yet
            return@Interceptor chain.proceed(originalRequest)
        }

        // Check if v4 Read Access Token (starts with eyJ) or v3 api_key
        val newRequest = if (key.startsWith("eyJ")) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $key")
                .header("accept", "application/json")
                .build()
        } else {
            val newUrl = originalUrl.newBuilder()
                .addQueryParameter("api_key", key)
                .build()
            originalRequest.newBuilder()
                .url(newUrl)
                .header("accept", "application/json")
                .build()
        }

        chain.proceed(newRequest)
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    val apiService: TmdbApiService = Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org/")
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(TmdbApiService::class.java)
}
