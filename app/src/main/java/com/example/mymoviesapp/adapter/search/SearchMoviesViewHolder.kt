package com.example.mymoviesapp.adapter.search

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.mymoviesapp.R
import com.example.mymoviesapp.api.APIClient
import com.example.mymoviesapp.api.MovieResult
import com.example.mymoviesapp.databinding.ItemSearchMovieBinding
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class SearchMoviesViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemSearchMovieBinding.bind(view)

    fun render(movie: MovieResult, onSearchMovieClicked: (MovieResult) -> Unit) {

        val path: String = APIClient.IMAGE_PATH_W185 + movie.posterPath
        Picasso.get().load(path)
            .into(binding.btnSearchedMovieCover, object : Callback {
                override fun onSuccess() {
                    binding.tvSearchedMovieTitle.text = ""
                    binding.tvSearchedMovieTitle.visibility = View.GONE
                }

                override fun onError(e: Exception?) {
                    binding.btnSearchedMovieCover.setImageResource(R.drawable.image_not_found)

                    binding.tvSearchedMovieTitle.text = movie.title
                    binding.tvSearchedMovieTitle.visibility = View.VISIBLE
                }
            })

        binding.btnSearchedMovieCover.setOnClickListener {
            onSearchMovieClicked(movie)
        }

    }
}