package com.example.mymoviesapp.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.mymoviesapp.api.MovieResult

class MoviesResultDiffUtil(
    private val oldList:List<MovieResult>,
    private val newList:List<MovieResult>
):DiffUtil.Callback() {

    override fun getOldListSize() = this.oldList.size
    override fun getNewListSize() = this.newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}