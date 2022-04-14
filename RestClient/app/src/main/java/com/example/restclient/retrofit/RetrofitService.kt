package com.example.restclient.retrofit

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitService {
    private var retrofit: Retrofit? = null

    fun getRetrofit(baseUrl: String): Retrofit? {
        // Logging-Interceptor
        // 1. OkHttp 인스턴스 생성
        val client = OkHttpClient.Builder()

        // 2. logging-interceptor 생성
        val loggingInterceptor = HttpLoggingInterceptor(object: HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                Log.d("Logging-Interceptor", message)
            }
        })

        // 3. Level 설정 후 logging-interceptor를 OkHttpClient에 추가
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        client.addInterceptor(loggingInterceptor)

        // 4. 설정된 OkHttpClient를 Retrofit 빌더에 추가
        // Retrofit Builder
        if(retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client.build()) // OkHttpClient
                .build()
        }
        return retrofit
    }
}