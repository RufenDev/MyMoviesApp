package com.example.mymoviesapp.adapter.discover

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.mymoviesapp.R
import com.example.mymoviesapp.api.APIClient
import com.example.mymoviesapp.api.MovieGenre
import com.example.mymoviesapp.api.Movies
import com.example.mymoviesapp.databinding.ItemDiscoverMovieBinding
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class DiscoverMoviesViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemDiscoverMovieBinding.bind(view)

    fun render(movie: Movies, onMovieGenreClicked: (MovieGenre) -> Unit) {
        binding.tvDiscoverHomeName.text = movie.genre.name

        val path = APIClient.IMAGE_PATH_W185 + movie.movieList.first().posterPath
        Picasso.get().load(path).into(
            binding.ivDiscoverMovieCover,
            object : Callback {
                override fun onSuccess() {}

                override fun onError(e: Exception?) {
                    binding.ivDiscoverMovieCover.setImageResource(R.drawable.image_not_found)
                }
            }
        )

        itemView.setOnClickListener {
            onMovieGenreClicked(movie.genre)
        }

    }


}