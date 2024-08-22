package com.example.haberuygulamasi.retrofit

import com.example.haberuygulamasi.servis.NewsApiService
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


// retrofit isteği yapacak object
object RetrofitInstance {

    private const val BASE_URL = "https://newsapi.org/v2/"
    val api : NewsApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL) // url verildi
            .addConverterFactory(GsonConverterFactory.create()) // json verileri olarak gelen verileri kotline nesnelerine çevirecek
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create()) // RxJava kullanarak asenkron olarak işlem yapılacak
            .build()// inşa edildi
            .create(NewsApiService::class.java) // NewsApiService ni oluşturuyoruz bağlıyoruz biribirine
    }
}