package com.example.mymoviesapp.adapter.seemore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.mymoviesapp.R
import com.example.mymoviesapp.api.DetailsItem
import com.example.mymoviesapp.interfaces.MovieConstants.LOADING_DISPLAY
import com.example.mymoviesapp.interfaces.MovieConstants.LOADING_ID
import com.example.mymoviesapp.interfaces.MovieConstants.NORMAL_DISPLAY

class SeeMoreAdapter(
    private var seeMoreList: List<DetailsItem> = emptyList(),
    private val onItemClicked: (DetailsItem) -> Unit
) : Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == NORMAL_DISPLAY) {
            SeeMoreViewHolder(
                inflater.inflate(R.layout.item_see_more, parent, false)
            )
        } else {
            SeeMoreLoadingViewHolder(
                inflater.inflate(R.layout.item_movie_loading, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder is SeeMoreViewHolder) {
            holder.render(seeMoreList[position], onItemClicked)
        }
    }

    override fun getItemCount() = seeMoreList.size

    override fun getItemViewType(position: Int) =
        if (seeMoreList[position].id == LOADING_ID) LOADING_DISPLAY
        else NORMAL_DISPLAY

    fun updateList(newItems: List<DetailsItem>, loadMore: Boolean) {
        val newList =
            if (!loadMore) newItems
            else {
                val columns = 3
                val remainder = newItems.size % columns
                val rest = columns - remainder
                newItems.plus(
                    List(rest) {
                        DetailsItem(false, LOADING_ID, "", "")
                    }
                )
            }

        val diffUtil = SeeMoreDiffUtil(seeMoreList, newList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)

        seeMoreList = newList

        diffResult.dispatchUpdatesTo(this)
    }

    private data class SeeMoreLoadingViewHolder(val view: View) : ViewHolder(view)
}