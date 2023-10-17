package com.example.mymoviesapp.room

import android.content.Context
import androidx.room.Room

object FavoriteMovieRoom {

    fun favoriteMoviesRoom(context: Context) =
        Room.databaseBuilder(
            context,
            FavoriteMovieDatabase::class.java,
            "favorite_movies"
        ).build()
}