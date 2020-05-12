package com.gvn.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.gvn.myapplication.databinding.ItemArticleBinding


class ArticleAdapter(private val listener: OnArticleClickListener) :
    RecyclerView.Adapter<ArticleAdapter.ViewHolder>() {

    private var listArticles: List<Article> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
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

    fun updateData(newList: List<Article>) {
        val diffResult = DiffUtil.calculateDiff(MyDiffCallBack(listArticles, newList))
        listArticles = newList
        diffResult.dispatchUpdatesTo(this)
    }

    class ViewHolder(val binding: ItemArticleBinding) : RecyclerView.ViewHolder(binding.root)

}

interface OnArticleClickListener {
    fun onItemClick(view: View, item: Article, position: Int)
}

class MyDiffCallBack(private val oldList: List<Article>, private val newList: List<Article>) :
    DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] == newList[newItemPosition]

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition].url == newList[newItemPosition].url
}