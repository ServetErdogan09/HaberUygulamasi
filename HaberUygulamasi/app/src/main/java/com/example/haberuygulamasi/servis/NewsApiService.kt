package com.example.haberuygulamasi.servis

import com.example.haberuygulamasi.model.NewsResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {
    @GET("everything")
    fun getNews(
        @Query("q") query: String, // aranan kelime
        @Query("from") fromDate: String, // başlangıç tarihi
        @Query("to") toDate: String, // bitiş tarihi
        @Query("sortBy") sortBy: String, // popularity
        @Query("apiKey") apiKey: String // key
    ): Single<NewsResponse>
}