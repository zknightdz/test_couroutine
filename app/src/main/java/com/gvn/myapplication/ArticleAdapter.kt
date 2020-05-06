package com.gvn.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gvn.myapplication.databinding.ItemArticleBinding

class ArticleAdapter(
    private val listArticles: List<Article>,
    private val listener: OnArticleClickListener
) :
    RecyclerView.Adapter<ArticleAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater  = LayoutInflater.from(parent.context)
        val binding = ItemArticleBinding.inflate(layoutInflater)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = listArticles.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.article = listArticles[position]
        holder.binding.executePendingBindings()

        holder.binding.root.setOnClickListener {
            listener.onItemClick(it, listArticles[position], position)
        }
    }

    class ViewHolder(val binding: ItemArticleBinding) : RecyclerView.ViewHolder(binding.root)

}

interface OnArticleClickListener {
    fun onItemClick(view: View, item: Article, position: Int)
}