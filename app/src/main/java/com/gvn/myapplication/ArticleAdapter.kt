package com.gvn.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_article.view.*

class ArticleAdapter(
    private val listArticles: List<Article>,
    private val listener: OnArticleClickListener
) :
    RecyclerView.Adapter<ArticleAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_article, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = listArticles.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(holder.itemView.context).load(listArticles[position].urlToImage)
            .into(holder.itemView.image)

        holder.itemView.title.text = listArticles[position].title
        holder.itemView.description.text = listArticles[position].description
        holder.itemView.time.text = listArticles[position].publishedAt
        holder.itemView.site.text = listArticles[position].source.name

        holder.itemView.setOnClickListener {
            listener.onItemClick(it, listArticles[position], position)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}

interface OnArticleClickListener {
    fun onItemClick(view: View, item: Article, position: Int)
}