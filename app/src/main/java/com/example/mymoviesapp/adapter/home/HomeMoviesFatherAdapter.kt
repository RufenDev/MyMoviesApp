package com.example.mymoviesapp.adapter.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mymoviesapp.interfaces.MovieAction
import com.example.mymoviesapp.R
import com.example.mymoviesapp.adapter.MoviesDiffUtil
import com.example.mymoviesapp.api.MovieGenre
import com.example.mymoviesapp.api.Movies
import com.example.mymoviesapp.interfaces.MovieConstants.LOADING_DISPLAY
import com.example.mymoviesapp.interfaces.MovieConstants.LOADING_ID
import com.example.mymoviesapp.interfaces.MovieConstants.NORMAL_DISPLAY

class HomeMoviesFatherAdapter(
    private var moviesList: List<Movies> = emptyList(),
    private val onMovieAction: (MovieAction) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return if (moviesList[position].genre.id == LOADING_ID) LOADING_DISPLAY
        else NORMAL_DISPLAY
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == NORMAL_DISPLAY) {
            HomeMoviesFatherViewHolder(
                inflater.inflate(R.layout.item_home_movies, parent, false)
            )

        } else {
            HomeMoviesLoadingViewHolder(
                inflater.inflate(R.layout.item_home_loading, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HomeMoviesFatherViewHolder) {
            holder.render(moviesList[position], onMovieAction)
        }
    }

    override fun getItemCount() = moviesList.size

    fun updateHomeMovies(moviesList: List<Movies>, loadMore: Boolean) {
        val newList = if (!loadMore) moviesList
        else moviesList.plus(Movies(MovieGenre(LOADING_ID, ""), emptyList()))

        val diffUtil = MoviesDiffUtil(this.moviesList, newList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)

        this.moviesList = newList
        diffResult.dispatchUpdatesTo(this)
    }

    class HomeMoviesLoadingViewHolder(view: View) : RecyclerView.ViewHolder(view)
}