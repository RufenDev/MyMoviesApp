package com.example.mymoviesapp.adapter.home

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mymoviesapp.interfaces.MovieAction
import com.example.mymoviesapp.api.Movies
import com.example.mymoviesapp.databinding.ItemHomeMoviesBinding

class HomeMoviesFatherViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemHomeMoviesBinding.bind(view)

    fun render(
        moviesByGenre: Movies,
        onMovieAction: (MovieAction) -> Unit
    ) {
        val capitalize = moviesByGenre.genre.name.lowercase().replaceFirstChar { it.uppercase() }
        binding.tvHomeMovieCategoryTitle.text = capitalize

        binding.rvHomeMoviesDisplay.layoutManager =
            LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)

        binding.rvHomeMoviesDisplay.adapter =
            HomeMoviesChildAdapter(
                moviesByGenre = moviesByGenre,
            ) { action ->
                onMovieAction(action)
            }
    }
}