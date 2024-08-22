package com.example.haberuygulamasi

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.room.Room
import com.example.haberuygulamasi.Database.AppDatabase
import com.example.haberuygulamasi.databinding.ActivityMainBinding
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navHostFragment: NavHostFragment
    lateinit var database: AppDatabase
    private var haber : String? =null
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        navController = NavController(this)

        val rastgeleString = arrayListOf("Türkiye Gündemi","Bilim","Savaş","Sağlık","Ekonomi","Siyaset")
        val randomIndex = Random.nextInt(rastgeleString.size)
        Log.e("MainActivity","seçilenIndex : $randomIndex")
        haber =  rastgeleString[randomIndex]
        Log.e("MainActivity","yazılan haber : $haber")


        // Veritabanını oluştur
        database = Room.databaseBuilder(this, AppDatabase::class.java, "new-database-name")
            .fallbackToDestructiveMigration()
            .build()



        // Toolbar'ı ayarla
        setSupportActionBar(binding.toolbar)

        // Navigation ve drawer setup
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.main_nav_fragment) as NavHostFragment
        NavigationUI.setupWithNavController(binding.drawerMainMenu, navHostFragment.navController)

        val toggle = ActionBarDrawerToggle(this, binding.drawer, binding.toolbar, 0, 0)
        binding.drawer.addDrawerListener(toggle)
        toggle.syncState()


        val toggle2 = ActionBarDrawerToggle(this, binding.drawer, binding.favoriTolbar, 0, 0)
        binding.drawer.addDrawerListener(toggle2)
        toggle2.syncState()

        defaultDate(haber!!)


        // Menü öğelerinin dinleyicilerini ayarla
        binding.drawerMainMenu.setNavigationItemSelectedListener { menuItem ->
            val category = when (menuItem.itemId) {
                R.id.category_technology -> "teknoloji"
                R.id.category_sports -> "spor"
                R.id.category_entertainment -> "eğlence"
                R.id.category_business -> "iş"
                R.id.category_health -> "sağlık"
                R.id.category_science -> "bilim"
                R.id.category_general -> "genel"
                R.id.category_politics -> "politika"
                R.id.category_favoriler -> "favoriler"
                else -> "genel"
            }

            Log.e("category", category)

            // Seçilen kategori ile `NewsFragment`'a git
            val bundle = Bundle().apply {
                putString("category", category)
            }

            val destination = if (category != "favoriler") R.id.newsFragment else R.id.favoriFragment
            navHostFragment.navController.navigate(destination, bundle)
            binding.drawer.closeDrawers()
            true
        }

        // Drawer başlığı ayarla
        val baslik = binding.drawerMainMenu.inflateHeaderView(R.layout.navigation_baslik)
        val textBaslik: TextView = baslik.findViewById(R.id.textBaslikMenu)
        textBaslik.text = "KATEGORİLER"
    }



    // search iconu oluşturma
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search_action, menu)
        val searchItem = menu?.findItem(R.id.search_icon)
        val searchView = searchItem?.actionView as androidx.appcompat.widget.SearchView

        searchView.queryHint = ""



        searchView.setOnClickListener {
            searchView.isIconified = false
            searchView.requestFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(searchView.findFocus(), InputMethodManager.SHOW_IMPLICIT)
        }

        searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Arama sorgusunu işle
                if (query.isNullOrBlank()){
                    defaultDate(haber!!)
                }else{
                    val bundle = Bundle().apply {
                        putString("category", query)
                    }
                    //searchView.isIconified = false
                    navHostFragment.navController.navigate(R.id.newsFragment, bundle)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Arama sorgusu her değiştiğinde işle
                if(newText.isNullOrBlank()){
                    defaultDate(haber!!)
                }else{
                    val bundle = Bundle().apply {
                        putString("category", newText)
                    }
                    navHostFragment.navController.navigate(R.id.newsFragment, bundle)
                }
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }


    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {

        val navController = findNavController(R.id.main_nav_fragment)
        val currentDestination = navController.currentDestination

        if (currentDestination?.id == R.id.favoriFragment){
            menu?.findItem(R.id.search_icon)?.isVisible = false
            binding.toolbar.visibility = View.GONE
            binding.favoriTolbar.visibility = View.VISIBLE
        }else{
            menu?.findItem(R.id.search_icon)?.isVisible = true
            binding.toolbar.visibility = View.VISIBLE
            binding.favoriTolbar.visibility = View.GONE
        }

        return super.onPrepareOptionsMenu(menu)
    }



    private fun defaultDate(haber : String){

        // Varsayılan kategoriyi ayarla
        val defaultbundle = Bundle().apply {
            putString("category", haber)
        }
        navHostFragment.navController.navigate(R.id.newsFragment, defaultbundle)

    }

    override fun onResume() {
        super.onResume()
        // Menü öğelerinin yeniden düzenlenmesini sağla
        this.invalidateOptionsMenu()
    }



}
