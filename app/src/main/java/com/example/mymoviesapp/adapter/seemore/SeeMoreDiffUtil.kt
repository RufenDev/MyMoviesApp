package com.example.mymoviesapp.adapter.seemore

import androidx.recyclerview.widget.DiffUtil
import com.example.mymoviesapp.api.DetailsItem

class SeeMoreDiffUtil(
    private val oldList:List<DetailsItem>,
    private val newList:List<DetailsItem>
): DiffUtil.Callback() {

    override fun getOldListSize() = this.oldList.size
    override fun getNewListSize() = this.newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}