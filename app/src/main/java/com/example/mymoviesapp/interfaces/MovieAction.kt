package com.example.mymoviesapp.interfaces

import android.view.View
import com.example.mymoviesapp.api.MovieGenre
import com.example.mymoviesapp.api.MovieResult
import com.example.mymoviesapp.room.FavoriteMovie

sealed interface MovieAction {
    class ShowMovieDetails(val movie: MovieResult) : MovieAction

    class ShowMoreGenres(val genre:MovieGenre) : MovieAction

    class SelectMovie(val movie: MovieResult, val viewToFocus:View) : MovieAction

    class AddToFavorite(val movie:FavoriteMovie, val add:Boolean) : MovieAction
}