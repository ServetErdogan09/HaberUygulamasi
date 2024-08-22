package com.example.haberuygulamasi.fragment

import com.example.haberuygulamasi.Database.AppDatabase
import java.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.haberuygulamasi.MainActivity
import com.example.haberuygulamasi.adapter.NewsAdapter
import com.example.haberuygulamasi.databinding.FragmentNewsBinding
import com.example.haberuygulamasi.model.NewsArticle
import com.example.haberuygulamasi.model.NewsResponse
import com.example.haberuygulamasi.retrofit.RetrofitInstance
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.Locale

class NewsFragment : Fragment() {

    private lateinit var binding: FragmentNewsBinding
    private val disposable = CompositeDisposable()
    private var sevenDaysAgo = ""
    private var todayDate = ""
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var database: AppDatabase

    // DURATION_LIMIT sabitini tanımlıyoruz
    companion object {
        const val DURATION_LIMIT = 7L // Örneğin 7 gün
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewsBinding.inflate(inflater, container, false)

        // Veritabanını al
        database = (activity as MainActivity).database

        // Tarihleri ayarlıyoruz
        sevenDaysAgo = getDateDaysAgo(DURATION_LIMIT.toInt()) // DURATION_LIMIT burada kullanılıyor
        todayDate = getTodayDate()


        // Haberleri çek
        newsDataIntent()

        return binding.root
    }

    private fun fetchNewsFromApi(category: String) {
        disposable.add(
            RetrofitInstance.api.getNews(query = category, fromDate = sevenDaysAgo, toDate = todayDate, sortBy = "popularity", apiKey = "df035048973d418580f8a58076148033")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<NewsResponse>() {
                    override fun onSuccess(newsResponse: NewsResponse) {
                    val newsArticle = newsResponse.articles
                        adapter(newsArticle)
                }
                    override fun onError(e: Throwable) {
                        Log.e("fragmantNews","veri çekerken hata oluştu: ${e.message}")
                    }

                })
        )

    }


    override fun onDestroyView() {
        super.onDestroyView()
        disposable.clear()
    }

    private fun getDateDaysAgo(daysAgo: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -daysAgo)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun getTodayDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun adapter(list: MutableList<NewsArticle>) {
        newsAdapter = NewsAdapter(list, requireContext(),database.newsDao())
        binding.newsReceyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.newsReceyclerview.adapter = newsAdapter
    }

    private fun newsDataIntent() {
        val sharedPreferences = requireActivity().getSharedPreferences("my_prefs", android.content.Context.MODE_PRIVATE)
        val data = sharedPreferences.getString("gundem", "Türkiye Gündem")
        val deger = sharedPreferences.getString("deger", "0")

        if (deger == "1") {
            data?.let {
                fetchNewsFromApi(it)
                Log.d("NewsFragment", "deger == 1, fetching news for: $data")
            }
            val oldData = sharedPreferences.edit()
            oldData.remove("deger")
            oldData.apply()
            Log.d("NewsFragment", "deger removed from SharedPreferences")
        } else {
            val category = arguments?.getString("category")
            val sharedPreferences = requireActivity().getSharedPreferences("newcategory", android.content.Context.MODE_PRIVATE)
            val categoryEdit = sharedPreferences.edit()
            categoryEdit.putString("nameCategory", category)
            categoryEdit.apply()
            if (category.isNullOrBlank()) {
                Log.e("NewsFragment", "Category argument is missing")
                return
            }
            fetchNewsFromApi(category)
            Log.d("NewsFragment", "deger != 1, fetching news for category: $category")
        }
    }

}
