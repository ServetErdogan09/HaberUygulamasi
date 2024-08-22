package com.example.haberuygulamasi.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.haberuygulamasi.Database.AppDatabase
import com.example.haberuygulamasi.MainActivity
import com.example.haberuygulamasi.R
import com.example.haberuygulamasi.adapter.favoriAdapter
import com.example.haberuygulamasi.databinding.FragmentFavoriBinding
import com.example.haberuygulamasi.model.RoomModel
import com.example.haberuygulamasi.servis.NewsDao
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class FavoriFragment : Fragment() {

    private lateinit var database: AppDatabase
    private lateinit var newsDao: NewsDao
    private lateinit var disposable: CompositeDisposable
    private lateinit var binding: FragmentFavoriBinding
    private lateinit var favoriAdapter: favoriAdapter // Sınıf ismini büyük harfle başlatın

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentFavoriBinding.inflate(inflater, container, false)

        // veri tabanını al
        database = (activity as MainActivity).database
        newsDao = database.newsDao() // newsDao'yu başlatın

        disposable = CompositeDisposable()
        getAllFavorites()
        setHasOptionsMenu(true)



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.newsFragment)
                }

            })


        }



    private fun getAllFavorites() {
        disposable.add(newsDao.getFavoritesAll()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ favoriteList ->
                setupAdapter(favoriteList)
            }, { error ->
                Log.e("Hata", "${error.message}")
            })

        )
    }

    private fun setupAdapter(modelR: List<RoomModel>) {
        if (modelR.isEmpty()) {
            Log.e("FavoriFragment", "Favori listesi boş.")
            return
        }

        favoriAdapter = favoriAdapter(modelR,newsDao,requireContext()) // Sınıf ismini büyük harfle başlatın
        binding.recyclerViewFavori.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewFavori.adapter = favoriAdapter
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear() // Bellek sızıntılarını önlemek için disposable'ı temizleyin
    }



}
