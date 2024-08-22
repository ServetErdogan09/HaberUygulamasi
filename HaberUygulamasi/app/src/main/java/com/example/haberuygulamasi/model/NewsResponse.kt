package com.example.haberuygulamasi.model


data class NewsResponse(
    val status: String,
    val totalResults: Int,
    val articles: MutableList<NewsArticle>
)

data class NewsArticle(
    val title: String,
    val description: String? = null,
    val url: String? = null,
    val urlToImage: String? = null,
    val publishedAt: String? = null,
    val content: String? = null,
    val author: String? = null,
    val storedAt: Long = System.currentTimeMillis(),
    val source: Source
)

data class Source(
    val id: String?,
    val name: String?
)
