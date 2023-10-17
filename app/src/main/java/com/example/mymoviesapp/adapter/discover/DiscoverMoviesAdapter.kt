package com.example.mymoviesapp.adapter.discover

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mymoviesapp.R
import com.example.mymoviesapp.adapter.MoviesDiffUtil
import com.example.mymoviesapp.api.MovieGenre
import com.example.mymoviesapp.api.Movies

class DiscoverMoviesAdapter(
    private var genreList:List<Movies> = emptyList(),
    private val onMovieGenreClicked: (MovieGenre) -> Unit,
) : RecyclerView.Adapter<DiscoverMoviesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = DiscoverMoviesViewHolder (
        LayoutInflater.from(parent.context).inflate(R.layout.item_discover_movie, parent, false)
    )

    override fun onBindViewHolder(holder: DiscoverMoviesViewHolder, position: Int) {
        holder.render(genreList[position], onMovieGenreClicked)
    }

    override fun getItemCount() = genreList.size

    fun updateDiscoverGenre(newList:List<Movies>){
        val diffUtil = MoviesDiffUtil(this.genreList, newList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)

        this.genreList = newList

        diffResult.dispatchUpdatesTo(this)
    }
}