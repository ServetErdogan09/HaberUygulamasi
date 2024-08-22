package com.example.haberuygulamasi.servis

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.haberuygulamasi.model.RoomModel
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
interface NewsDao {
    @Insert
    fun insert(news: RoomModel): Completable

    @Query("DELETE FROM news_articles_new WHERE id = :id")
    fun removeFromFavorites(id: Long): Completable

    @Query("DELETE  FROM news_articles_new WHERE title = :title")
    fun deleteByTitle(title: String): Completable

    @Query("SELECT * FROM news_articles_new WHERE  title = :title  ")
    fun getAllFavorites(title: String): Single<MutableList<RoomModel>>

    @Query("SELECT * FROM news_articles_new WHERE title = :title")
    fun getNewsByTitle(title: String): Single<MutableList<RoomModel>>

    @Query("SELECT COUNT(*) FROM news_articles_new WHERE title = :title") // BU BAŞLIKTAN KAÇTANE VARSA SAYISINI BANA VERECEK
    fun countByTitle(title: String): Single<Int>

    @Query("SELECT * FROM news_articles_new ")
    fun getFavoritesAll(): Single<MutableList<RoomModel>>

    @Query("SELECT COUNT(*)  FROM news_articles_new ")
    fun countFavori(): Single<Int>
}
