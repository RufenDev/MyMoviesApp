package com.example.mymoviesapp.api

import com.google.gson.annotations.SerializedName

data class MoviesGenreResponse(
    @SerializedName("genres") val genres: List<MovieGenre>
)

data class MovieGenre(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("name") val name: String = ""
)