package com.example.mymoviesapp.api

import com.google.gson.annotations.SerializedName

data class MoviesResponse(
    @SerializedName("page")
    val page: Int,

    @SerializedName("total_pages")
    val totalPages: Int,

    @SerializedName("results")
    val results: List<MovieResult>
)

data class MovieResult(
    @SerializedName("adult")
    val adult: Boolean = false,

    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("title")
    val title: String = "",

    @SerializedName("poster_path")
    val posterPath: String = "",
)

data class Movies(
    val genre: MovieGenre,
    val movieList: List<MovieResult>
)

/*
data class MoviesAllDetailsResponse(
    @SerializedName("page")
    val page: Int,

    @SerializedName("results")
    val results: List<MovieAllDetails>,

    @SerializedName("total_pages")
    val total_pages: Int,

    @SerializedName("total_results")
    val total_results: Int
)

data class MovieAllDetails (
    @SerializedName("adult")
    val adult: Boolean,

    @SerializedName("backdrop_path")
    val backdrop_path: String,

    @SerializedName("genre_ids")
    val genre_ids: List<Int>,

    @SerializedName("id")
    val id: Int,

    @SerializedName("original_language")
    val original_language: String,

    @SerializedName("original_title")
    val original_title: String,

    @SerializedName("overview")
    val overview: String,

    @SerializedName("popularity")
    val popularity: Double,

    @SerializedName("poster_path")
    val poster_path: String,

    @SerializedName("release_date")
    val release_date: String,

    @SerializedName("title")
    val title: String,

    @SerializedName("video")
    val video: Boolean,

    @SerializedName("vote_average")
    val vote_average: Double,

    @SerializedName("vote_count")
    val vote_count: Int

)*/