package com.example.mymoviesapp.adapter.details

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mymoviesapp.R
import com.example.mymoviesapp.api.DetailsItem

class DetailsAdapter(
    private var itemList: List<DetailsItem> = emptyList(),
    private val onItemClicked: ((DetailsItem) -> Unit)
) : RecyclerView.Adapter<DetailsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = DetailsViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_details, parent, false)
    )

    override fun onBindViewHolder(holder: DetailsViewHolder, position: Int) {
        holder.render(itemList[position], onItemClicked)
    }

    override fun getItemCount() = itemList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateItemList(newItemList:List<DetailsItem>){
        itemList = newItemList
        notifyDataSetChanged()
    }

}