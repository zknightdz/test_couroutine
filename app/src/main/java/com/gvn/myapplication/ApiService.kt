package com.gvn.myapplication

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("everything?q=bitcoin&from=2020-03-13&sortBy=publishedAt&apiKey=6b4306242b4348a7aab6b052d656a33c")
    fun getListNews(): Call<NewsResponse>
}