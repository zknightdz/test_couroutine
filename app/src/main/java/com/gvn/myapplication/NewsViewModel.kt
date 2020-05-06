package com.gvn.myapplication

import android.icu.text.SimpleDateFormat
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.await
import java.util.*

class NewsViewModel(private val apiService: ApiService) : ViewModel() {
    private val _data = MutableLiveData<List<Article>>()
    val data: LiveData<List<Article>>
        get() = _data

    init {
        fetchData("Donal Trump")
    }

    fun fetchData(text: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val currentTime: Date = Calendar.getInstance().time
                val calendar = Calendar.getInstance()
                calendar.time = currentTime
                calendar.add(Calendar.DAY_OF_YEAR, -7)
                val newDate = calendar.time
                val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                val from = dateFormat.format(newDate)

                val map = mapOf(
                    "q" to text,
                    "from" to from,
                    "sortBy" to "publishedAt",
                    "apiKey" to "6b4306242b4348a7aab6b052d656a33c"
                )
                Log.d("NewsViewModel", map.toString())

                val response = apiService.getListNews(map).await()
                _data.postValue(response.articles)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("NewsViewModel", e.toString())
            }
        }
    }
}