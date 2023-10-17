package com.example.mymoviesapp.adapter.favorites

import androidx.recyclerview.widget.DiffUtil
import com.example.mymoviesapp.room.FavoriteMovie

class FavoriteDiffUtil(
    private val oldList: List<FavoriteMovie>,
    private val newList: List<FavoriteMovie>
) : DiffUtil.Callback() {

    override fun getOldListSize() = this.oldList.size
    override fun getNewListSize() = this.newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}