package com.gvn.myapplication

import com.google.gson.annotations.SerializedName

data class NewsResponse(
    @SerializedName("status") val status: String,
    @SerializedName("totalResults")  val totalResults: Long,
    @SerializedName("articles") val articles: List<Article>
)

data class Article(
    @SerializedName("source") val source: Source,
    @SerializedName("author") val author: String? = null,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("url") val url: String,
    @SerializedName("urlToImage") val urlToImage: String? = null,
    @SerializedName("publishedAt") val publishedAt: String,
    @SerializedName("content") val content: String
)

data class Source(
    @SerializedName("id") val id: String? = null,
    @SerializedName("name") val name: String
)
