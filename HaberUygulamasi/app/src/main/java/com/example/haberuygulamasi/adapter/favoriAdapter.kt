package com.example.haberuygulamasi.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.haberuygulamasi.MainActivity
import com.example.haberuygulamasi.databinding.RecyclerTasarimBinding
import com.example.haberuygulamasi.fragment.FavoriFragmentDirections
import com.example.haberuygulamasi.model.RoomModel
import com.example.haberuygulamasi.servis.NewsDao
import com.squareup.picasso.Picasso
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class favoriAdapter(private var favoriListe: List<RoomModel>, private val newsDao: NewsDao,val context: Context) : RecyclerView.Adapter<favoriAdapter.FavoriHolder>() {

    private val disposable = CompositeDisposable()

    class FavoriHolder(val binding: RecyclerTasarimBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriHolder {
        val binding = RecyclerTasarimBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriHolder(binding)
    }

    override fun getItemCount(): Int {
        return favoriListe.size
    }

    override fun onBindViewHolder(holder: FavoriHolder, position: Int) {
        val dataFavori = favoriListe[position]

        // Verileri bağlama
        holder.binding.sourcNameText.text = dataFavori.sourcName
        holder.binding.titleTextView.text = dataFavori.title
        holder.binding.hoursAgeText.text = dataFavori.publishedAt

        holder.binding.titleTextView.setOnClickListener {
            val action = FavoriFragmentDirections.actionFavoriFragmentToDetailedFragment(
                "", urlFavori = dataFavori.url.toString()
            )
            holder.itemView.findNavController().navigate(action)
        }

        holder.binding.urlToImage.setOnClickListener {
            val action = FavoriFragmentDirections.actionFavoriFragmentToDetailedFragment(
                "", urlFavori = dataFavori.url.toString()
            )
            holder.itemView.findNavController().navigate(action)
        }

        // Görsel ekleme
        if (!dataFavori.urlToImage.isNullOrBlank()) {
            Picasso.get()
                .load(dataFavori.urlToImage)
                .into(holder.binding.urlToImage)
        }

        holder.binding.favoritImageView2.isVisible = true
        holder.binding.favoritImageView2.setOnClickListener {
            deleteFavorite(dataFavori.title.toString(),holder)
        }
    }

    private fun deleteFavorite(title: String,holder: FavoriHolder) {
        disposable.add(newsDao.deleteByTitle(title)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally { getAllFavorites(holder) } // Silme işleminden sonra favori listesini güncelle
            .subscribe({
                Log.e("favoriteAdapter", "Favori Silindi: $title")
            }, { error ->
                Log.e("favoriteAdapter", "Favori silinmedi: ${error.message}")
            })
        )
    }

    private fun getAllFavorites(holder: FavoriHolder) {
        disposable.add(newsDao.getFavoritesAll()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ favoritListe ->
                if (favoritListe.isEmpty()){
                    // newsFragmanata geçiş yap
                    val intet = Intent(holder.itemView.context,MainActivity::class.java)
                    holder.itemView.context.startActivity(intet)
                    Log.e("liste","liste Boş")
                }else{
                    updateList(favoritListe)
                }

            }, { error ->
                Log.e("Hata", "Veri alınırken bir hata oluştu: ${error.message}")
            })
        )
    }

    private fun updateList(newList: List<RoomModel>) {
        favoriListe = newList
        notifyDataSetChanged()
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        disposable.clear()
    }

}
