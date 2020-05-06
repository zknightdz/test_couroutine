package com.gvn.myapplication

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface ApiService {
    @GET("everything")
    fun getListNews(@QueryMap options: Map<String, String>): Call<NewsResponse>
}