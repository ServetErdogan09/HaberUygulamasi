package com.example.haberuygulamasi.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.haberuygulamasi.converts.Converters

@Entity("news_articles_new")
@TypeConverters(Converters::class)
data class RoomModel(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0, // Otomatik artan bir ID
    val title: String? = null,
    val urlToImage: String? = null,
    val url: String? = null,
    val publishedAt : String?=null,
    val sourcName : String?= null
)
