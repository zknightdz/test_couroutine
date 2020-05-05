package com.gvn.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.await
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity(), OnArticleClickListener {

    private var apiService: ApiService? = null
    private var adapter: ArticleAdapter? = null
    private var listArticles: ArrayList<Article> = arrayListOf()

    val handler = CoroutineExceptionHandler { _, exception ->
        Log.d("TAG", "$exception handled !")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        adapter = ArticleAdapter(listArticles, this)

        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }

        initRetrofit()

        GlobalScope.launch(Dispatchers.Main + handler) {
            val news = fetchData()
            Log.d("MainActivity", "${news?.totalResults}")
            news?.articles?.let {
                listArticles.clear()
                listArticles.addAll(it)
                adapter?.notifyDataSetChanged()
            }
        }
    }

    private fun initRetrofit() {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(logging)


        val retrofit = Retrofit.Builder()
            .baseUrl("http://newsapi.org/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build()

        apiService = retrofit.create(ApiService::class.java)
    }

    private suspend fun fetchData(): NewsResponse? {
        return GlobalScope.async(Dispatchers.IO) {
            return@async apiService?.getListNews()?.await()
        }.await()
    }

    override fun onItemClick(view: View, item: Article, position: Int) {
        Intent(this, WebViewActivity::class.java).apply {
            putExtra(KEY_URL, item.url)
            startActivity(this)
        }
    }

    companion object {
        val KEY_URL = "KEY_URL"
    }

}



