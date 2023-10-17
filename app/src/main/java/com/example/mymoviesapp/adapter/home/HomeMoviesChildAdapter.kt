package com.example.mymoviesapp.adapter.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.RecyclerView
import com.example.mymoviesapp.interfaces.MovieAction
import com.example.mymoviesapp.R
import com.example.mymoviesapp.api.MovieGenre
import com.example.mymoviesapp.api.Movies
import com.example.mymoviesapp.interfaces.MovieConstants.LOOK_MORE_DISPLAY
import com.example.mymoviesapp.interfaces.MovieConstants.NORMAL_DISPLAY
import com.example.mymoviesapp.interfaces.MovieConstants.POPULAR_ID

class HomeMoviesChildAdapter(
    private val moviesByGenre: Movies,
    private val onMovieAction: (MovieAction) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return if (moviesByGenre.genre.id == POPULAR_ID) {
            super.getItemViewType(position)

        } else if (position == moviesByGenre.movieList.size) {
            LOOK_MORE_DISPLAY

        } else {
            NORMAL_DISPLAY
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (moviesByGenre.genre.id == POPULAR_ID) {
            HomeMoviesChildPopularViewHolder(
                inflater.inflate(R.layout.item_home_movie_display_popular, parent, false)
            )

        } else if (viewType == NORMAL_DISPLAY) {
            HomeMoviesChildViewHolder(
                inflater.inflate(R.layout.item_home_movie_display, parent, false)
            )

        } else {
            HomeMoviesDisplayLookMoreViewHolder(
                inflater.inflate(R.layout.item_home_movie_display_look_more, parent, false)
            )
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HomeMoviesChildPopularViewHolder -> {
                holder.render(moviesByGenre.movieList[position], onMovieAction)
            }

            is HomeMoviesChildViewHolder -> {
                holder.render(moviesByGenre.movieList[position], onMovieAction)
            }

            is HomeMoviesDisplayLookMoreViewHolder -> {
                holder.render(moviesByGenre.genre, onMovieAction)
            }
        }
    }

    override fun getItemCount() =
        moviesByGenre.movieList.size + if (moviesByGenre.genre.id == POPULAR_ID) 0 else 1

    private class HomeMoviesDisplayLookMoreViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun render(
            genre: MovieGenre,
            onMovieAction: (MovieAction) -> Unit
        ) {

            val btnLookMore: AppCompatImageButton = itemView.findViewById(R.id.btnHomeLookMore)

            btnLookMore.setOnClickListener {
                onMovieAction(MovieAction.ShowMoreGenres(genre))
            }
        }
    }
}