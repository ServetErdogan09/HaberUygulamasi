package com.example.haberuygulamasi.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.navOptions
import com.example.haberuygulamasi.R
import com.example.haberuygulamasi.databinding.FragmentDetaliledBinding

class DetailedFragment : Fragment() {

    private var _binding: FragmentDetaliledBinding? = null
    private val binding get() = _binding!!

    private lateinit var backPressedCallback: OnBackPressedCallback
    private lateinit var navController: NavController
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferences2: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentDetaliledBinding.inflate(inflater, container, false)

        requireActivity().findViewById<Toolbar>(R.id.toolbar).visibility =View.GONE
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Argumentleri al
        val args = arguments?.let { DetailedFragmentArgs.fromBundle(it) }
        val articleUrlOrHtml = args?.url
        val articleFavori = args?.urlFavori

        // NavController'ı başlat
        navController = Navigation.findNavController(requireActivity(), R.id.main_nav_fragment)

        // SharedPreferences'ı başlat
        sharedPreferences = requireActivity().getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        sharedPreferences2 = requireActivity().getSharedPreferences("newcategory", Context.MODE_PRIVATE)

        // OnBackPressedCallback'ı oluştur
        backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // SharedPreferences'a veri kaydet
                val editor = sharedPreferences.edit()
                val newCategory = sharedPreferences2.getString("nameCategory", "Türkiye Gündemi")

                if (newCategory != null) {
                    editor.putString("gundem", newCategory)
                } else {
                    editor.putString("gundem", "Türkiye Gündemi") // Varsayılan değer
                }
                editor.putString("deger", "1")
                editor.apply()

                // Geri tuşuna basıldığında NewsFragment'a geçiş yap
                val navOptions = navOptions {
                    popUpTo(R.id.detailedFragment) { inclusive = true }
                }
                navController.navigate(R.id.action_detailedFragment_to_newsFragment, null, navOptions)
                Log.e("DetailedFragment", "NewsFragment'a geçiş yapıldı")
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backPressedCallback)

        val webView: WebView = binding.webView
        webView.settings.javaScriptEnabled = true

        // WebView içeriğini yükle
        if (!articleUrlOrHtml.isNullOrBlank()) {
            if (articleUrlOrHtml.startsWith("http") || articleUrlOrHtml.startsWith("https")) {
                webView.loadUrl(articleUrlOrHtml)
                Log.d("DetailedFragment", "Article URL: $articleUrlOrHtml")
            } else {
                webView.loadData(articleUrlOrHtml, "text/html", "UTF-8")
                Log.d("DetailedFragment", "HTML Content: $articleUrlOrHtml")
            }
        }

        if (!articleFavori.isNullOrBlank()) {
                if (articleFavori.startsWith("http") || articleFavori.startsWith("https")) {
                    webView.loadUrl(articleFavori)
                    Log.d("DetailedFragment", "Article URL: $articleUrlOrHtml")
                } else {
                    webView.loadData(articleFavori, "text/html", "UTF-8")
                    Log.d("DetailedFragment", "HTML Content: $articleUrlOrHtml")
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().findViewById<Toolbar>(R.id.toolbar).visibility =View.VISIBLE
        _binding = null
    }

}
