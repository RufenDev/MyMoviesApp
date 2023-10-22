package com.example.mymoviesapp.adapter.favorites

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.mymoviesapp.R
import com.example.mymoviesapp.api.APIClient.IMAGE_PATH_W342
import com.example.mymoviesapp.api.MovieResult
import com.example.mymoviesapp.databinding.ItemFavoriteMovieBinding
import com.example.mymoviesapp.interfaces.MovieAction
import com.example.mymoviesapp.interfaces.MovieAction.AddToFavorite
import com.example.mymoviesapp.interfaces.MovieAction.ShowMovieDetails
import com.example.mymoviesapp.room.FavoriteMovie
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class FavoritesViewHolder(view: View) : ViewHolder(view) {

    val binding = ItemFavoriteMovieBinding.bind(view)

    fun render(movie: FavoriteMovie, onFavoriteMovieAction: (MovieAction) -> Unit) {

        binding.checkFavorite.isChecked = movie.isInFavorite

        Picasso.get().load(IMAGE_PATH_W342 + movie.posterPath)
            .into(binding.ivFavoritePoster, object : Callback {
                override fun onSuccess() {
                    binding.tvFavoriteTitle.visibility = View.GONE
                    binding.layoutFavoriteLoading.visibility = View.GONE
                    binding.layoutFavoritePoster.visibility = View.VISIBLE
                }

                override fun onError(e: Exception?) {
                    binding.tvFavoriteTitle.text = movie.title
                    binding.ivFavoritePoster.setImageResource(R.drawable.image_not_found)

                    binding.layoutFavoriteLoading.visibility = View.GONE
                    binding.layoutFavoritePoster.visibility = View.VISIBLE
                    binding.tvFavoriteTitle.visibility = View.VISIBLE
                }
            })

        binding.layoutFavoritePoster.setOnClickListener {
            val movieDetail = MovieResult(
                id = movie.id,
                title = movie.title,
                posterPath = movie.posterPath
            )
            onFavoriteMovieAction(ShowMovieDetails(movieDetail))
        }

        binding.checkFavorite.setOnClickListener {
            onFavoriteMovieAction(
                AddToFavorite(
                    movie,
                    binding.checkFavorite.isChecked
                )
            )
        }
    }
}