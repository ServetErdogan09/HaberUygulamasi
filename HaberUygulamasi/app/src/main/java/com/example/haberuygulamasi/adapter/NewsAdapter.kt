package com.example.haberuygulamasi.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.haberuygulamasi.databinding.RecyclerTasarimBinding
import com.example.haberuygulamasi.fragment.NewsFragmentDirections
import com.example.haberuygulamasi.model.NewsArticle
import com.example.haberuygulamasi.model.RoomModel
import com.example.haberuygulamasi.servis.NewsDao
import com.squareup.picasso.Picasso
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

class NewsAdapter(
    private val newsList: MutableList<NewsArticle>,
    private val context: Context,
    private val newsDao: NewsDao
) : RecyclerView.Adapter<NewsAdapter.NewsHolder>() {

    private val disposable = CompositeDisposable()

    class NewsHolder(val tasarim: RecyclerTasarimBinding) : RecyclerView.ViewHolder(tasarim.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsHolder {
        val tasarim =
            RecyclerTasarimBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsHolder(tasarim)
    }

    override fun getItemCount(): Int = newsList.size

    override fun onBindViewHolder(holder: NewsHolder, position: Int) {

        val newsArticle = newsList[position]

        holder.tasarim.sourcNameText.text = newsArticle.source.name
        val dataTime = formatDateTime(newsArticle.publishedAt ?: "")
        holder.tasarim.hoursAgeText.text = dataTime
        holder.tasarim.titleTextView.text = newsArticle.title




        // RoomModel oluşturulması
        val model = RoomModel(
            title = newsArticle.title,
            urlToImage = newsArticle.urlToImage,
            url = newsArticle.url,
            publishedAt = dataTime,
            sourcName = newsArticle.source.name
        )
       checkIfFavorite(model,holder)


        holder.tasarim.favoritImageView1.setOnClickListener {
            addToFavorites(model, holder)
        }
        holder.tasarim.favoritImageView2.setOnClickListener {
            removeFromFavorites(model, holder)
        }



        if (!newsArticle.urlToImage.isNullOrBlank()) {
            Picasso.get()
                .load(newsArticle.urlToImage)
                .into(holder.tasarim.urlToImage)
        }

        holder.tasarim.titleTextView.setOnClickListener {
            val action = NewsFragmentDirections.actionNewsFragmentToDetailedFragment(
                url = newsArticle.url ?: "",
                ""
            )
            Log.e("tıklananHaber","başlık : ${newsArticle.title}")
            Log.e("tıklananHaber","url : ${newsArticle.url}")
            holder.itemView.findNavController().navigate(action)
        }

        holder.tasarim.urlToImage.setOnClickListener {
            val action = NewsFragmentDirections.actionNewsFragmentToDetailedFragment(
                url = newsArticle.url ?: "",
                ""
            )
            Log.e("tıklananHaber","başlık : ${newsArticle.title}")
            Log.e("tıklananHaber","url : ${newsArticle.url}")
            holder.itemView.findNavController().navigate(action)
        }

    }

    private fun addToFavorites(model: RoomModel, holder: NewsHolder) {
        // İlk olarak, bu başlığın veritabanında olup olmadığını kontrol edin
        disposable.add(
            newsDao.countByTitle(model.title!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ count ->
                    if (count == 0) {
                        // Eğer bu başlığa sahip haber veritabanında yoksa, ekleyin
                        disposable.add(
                            newsDao.insert(model)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    Log.d("newsAdapter", "Favori eklendi: ${model.title}")
                                    holder.tasarim.favoritImageView1.isVisible = false
                                    holder.tasarim.favoritImageView2.isVisible = true
                                }, { error ->
                                    Log.e("newsAdapter", "Ekleme hatası: ${error.message}")
                                })
                        )
                    } else {
                        // Başlık zaten mevcut, burada herhangi bir işlem yapmanıza gerek yok
                        Log.d("newsAdapter", "Başlık zaten mevcut: ${model.title}")
                    }
                }, { error ->
                    Log.e("newsAdapter", "Sorgu hatası: ${error.message}")
                })
        )
    }

    private fun removeFromFavorites(model: RoomModel, holder: NewsHolder) {
        // Bu başlığa sahip haber var mı kontrol edin
        disposable.add(
            newsDao.countByTitle(model.title!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ count ->
                    if (count > 0) {
                        // Eğer bu başlığa sahip haber varsa, silin
                        disposable.add(
                            newsDao.deleteByTitle(model.title)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    Log.d("newsAdapter", "Favori silindi: ${model.title}")
                                    holder.tasarim.favoritImageView1.isVisible = true
                                    holder.tasarim.favoritImageView2.isVisible = false
                                }, { error ->
                                    Log.e("newsAdapter", "Silme hatası: ${error.message}")
                                })
                        )
                    } else {
                        // Başlık mevcut değil, burada herhangi bir işlem yapmanıza gerek yok
                        Log.d("newsAdapter", "Başlık mevcut değil: ${model.title}")
                    }
                }, { error ->
                    Log.e("newsAdapter", "Sorgu hatası: ${error.message}")
                })
        )
    }


    private fun checkIfFavorite(model: RoomModel, holder: NewsHolder) {
        disposable.add(
            newsDao.getNewsByTitle(model.title.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ favoritList ->
                    if (favoritList.isNotEmpty()) {
                        holder.tasarim.favoritImageView1.isVisible = false
                        holder.tasarim.favoritImageView2.isVisible = true // Favori ise kırmızı kalp göster
                    } else {
                        holder.tasarim.favoritImageView1.isVisible = true
                        holder.tasarim.favoritImageView2.isVisible = false // Favori değilse beyaz kalp göster
                    }
                }, { error ->
                    Log.e("newsAdapter", "Hata: ${error.message}")
                })
        )
    }


    fun formatDateTime(isoDateTime: String): String {
        val dateTime = ZonedDateTime.parse(isoDateTime)
        val now = ZonedDateTime.now()

        val days = ChronoUnit.DAYS.between(dateTime, now)
        val hours = ChronoUnit.HOURS.between(dateTime, now) % 24
        val minutes = ChronoUnit.MINUTES.between(dateTime, now) % 60
        val seconds = ChronoUnit.SECONDS.between(dateTime, now) % 60

        return when {
            days > 0 -> "$days gün önce"
            hours > 0 -> "$hours saat önce"
            minutes > 0 -> "$minutes dakika önce"
            else -> "$seconds saniye önce"
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        disposable.clear()
    }


}

