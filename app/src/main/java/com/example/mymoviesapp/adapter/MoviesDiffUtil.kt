package com.example.mymoviesapp.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.mymoviesapp.api.Movies

class MoviesDiffUtil(
    private val oldList:List<Movies>,
    private val newList:List<Movies>
):DiffUtil.Callback() {

    override fun getOldListSize() = this.oldList.size
    override fun getNewListSize() = this.newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].genre == newList[newItemPosition].genre
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}