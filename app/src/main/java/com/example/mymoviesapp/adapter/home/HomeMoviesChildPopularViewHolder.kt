package com.example.mymoviesapp.adapter.home

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.mymoviesapp.interfaces.MovieAction
import com.example.mymoviesapp.R
import com.example.mymoviesapp.api.APIClient
import com.example.mymoviesapp.api.MovieResult
import com.example.mymoviesapp.databinding.ItemHomeMovieDisplayPopularBinding
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class HomeMoviesChildPopularViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemHomeMovieDisplayPopularBinding.bind(view)

    fun render(movie: MovieResult, onMovieAction: (MovieAction) -> Unit) {
        binding.tvHomeMoviePopularName.text = movie.title

        val path: String = APIClient.IMAGE_PATH_W780 + movie.posterPath
        Picasso.get().load(path).into(binding.btnHomeMoviePopularCover, object : Callback {
            override fun onSuccess() {}

            override fun onError(e: Exception?) {
                binding.btnHomeMoviePopularCover.setImageResource(R.drawable.image_not_found)
            }
        })

        binding.btnSelectPopularMovie.setOnClickListener {
            onMovieAction(MovieAction.SelectMovie(movie, it))
        }
        binding.btnHomeMoviePopularCover.setOnLongClickListener {
            onMovieAction(MovieAction.SelectMovie(movie, binding.btnSelectPopularMovie))
            true
        }
        binding.btnHomeMoviePopularCover.setOnClickListener {
            onMovieAction(MovieAction.ShowMovieDetails(movie))
        }
    }
}