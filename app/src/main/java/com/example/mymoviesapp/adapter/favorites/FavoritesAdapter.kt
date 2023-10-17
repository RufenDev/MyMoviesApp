package com.example.mymoviesapp.adapter.favorites

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.mymoviesapp.R
import com.example.mymoviesapp.interfaces.MovieAction
import com.example.mymoviesapp.room.FavoriteMovie

class FavoritesAdapter(
    private var favoritesList: List<FavoriteMovie> = emptyList(),
    private val onFavoriteMovieAction:(MovieAction) -> Unit
) : Adapter<FavoritesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = FavoritesViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_favorite_movie, parent, false)
    )

    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
        holder.render(favoritesList[position], onFavoriteMovieAction)
    }

    override fun getItemCount() = favoritesList.size

    fun updateFavoriteList(newList:List<FavoriteMovie>){
        val diffUtil = FavoriteDiffUtil(favoritesList, newList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)

        favoritesList = newList

        diffResult.dispatchUpdatesTo(this)
    }

}