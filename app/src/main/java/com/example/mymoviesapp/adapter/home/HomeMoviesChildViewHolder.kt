package com.example.mymoviesapp.adapter.home

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.mymoviesapp.interfaces.MovieAction
import com.example.mymoviesapp.R
import com.example.mymoviesapp.api.APIClient
import com.example.mymoviesapp.api.MovieResult
import com.example.mymoviesapp.databinding.ItemHomeMovieDisplayBinding
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class HomeMoviesChildViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemHomeMovieDisplayBinding.bind(view)

    fun render(movie: MovieResult, onMovieAction: (MovieAction) -> Unit) {

        binding.tvHomeMovieName.text = movie.title

        val path: String = APIClient.IMAGE_PATH_W342 + movie.posterPath
        Picasso.get().load(path).into(binding.btnHomeMovieCover, object : Callback {
            override fun onSuccess() {}

            override fun onError(e: Exception?) {
                binding.btnHomeMovieCover.setImageResource(R.drawable.image_not_found)
            }
        })

        binding.btnHomeMovieCover.setOnClickListener {
            onMovieAction(MovieAction.ShowMovieDetails(movie))
        }
        binding.btnHomeMovieCover.setOnLongClickListener {
            onMovieAction(MovieAction.SelectMovie(movie, binding.btnSelectHomeMovie))
            true
        }

        binding.btnSelectHomeMovie.setOnClickListener {
            onMovieAction(MovieAction.SelectMovie(movie, it))
        }

        itemView.setOnLongClickListener {
            onMovieAction(MovieAction.SelectMovie(movie, binding.btnSelectHomeMovie))
            true
        }
    }
}