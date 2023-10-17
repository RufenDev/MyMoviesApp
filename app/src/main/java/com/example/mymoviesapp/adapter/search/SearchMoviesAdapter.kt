package com.example.mymoviesapp.adapter.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mymoviesapp.R
import com.example.mymoviesapp.adapter.MoviesResultDiffUtil
import com.example.mymoviesapp.api.MovieResult
import com.example.mymoviesapp.interfaces.MovieConstants.LOADING_DISPLAY
import com.example.mymoviesapp.interfaces.MovieConstants.LOADING_ID
import com.example.mymoviesapp.interfaces.MovieConstants.NORMAL_DISPLAY

class SearchMoviesAdapter(
    private var searchMovies: List<MovieResult> = emptyList(),
    private val onSearchMovieClicked: (MovieResult) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return if (searchMovies[position].id == LOADING_ID) {
            LOADING_DISPLAY
        } else {
            NORMAL_DISPLAY
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == NORMAL_DISPLAY) {
            SearchMoviesViewHolder(
                inflater.inflate(R.layout.item_search_movie, parent, false)
            )
        } else {
            SearchMovieLoadingViewHolder(
                inflater.inflate(R.layout.item_movie_loading, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is SearchMoviesViewHolder) {
            holder.render(searchMovies[position], onSearchMovieClicked)
        }
    }

    override fun getItemCount() = searchMovies.size

    fun updateSearchedMovies(searchedMovies: List<MovieResult>, loadMore: Boolean) {
        val newList =
            if (!loadMore) searchedMovies
            else {
                val columns = 3
                val remainder = searchedMovies.size % columns
                val rest = columns - remainder
                searchedMovies.plus(List(rest) { MovieResult(false, LOADING_ID, "", "") })
            }

        val diffUtil = MoviesResultDiffUtil(searchMovies, newList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)

        searchMovies = newList

        diffResult.dispatchUpdatesTo(this)
    }

    data class SearchMovieLoadingViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}