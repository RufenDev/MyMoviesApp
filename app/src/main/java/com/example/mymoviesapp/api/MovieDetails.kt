package com.example.mymoviesapp.api

import com.google.gson.annotations.SerializedName

data class MovieDetails(
    @SerializedName("genres") val genres: List<MovieGenre>,
    @SerializedName("homepage") val homepage: String,
    @SerializedName("id") val id: Int,
    @SerializedName("overview") val synopsis: String,
    @SerializedName("poster_path") val posterPath: String,
    @SerializedName("production_companies") val productionCompanies: List<ProductionCompany>,
    @SerializedName("release_date") val releaseDate: String,
    @SerializedName("runtime") val duration: Int,
    @SerializedName("title") val title: String,
    @SerializedName("vote_average") val voteAverage: Double,
)

data class ProductionCompany(
    @SerializedName("id") val id: Int,
    @SerializedName("logo_path") val logoPath: String,
    @SerializedName("name") val name: String,
)

data class MovieCredits(
    @SerializedName("cast") val cast: List<MovieActor>,
    @SerializedName("crew") val crew: List<MovieCrew>,
)

data class MovieActor(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("character") val character: String,
    @SerializedName("profile_path") val photoPath: String
)

data class MovieCrew(
    @SerializedName("id") val id: Int,
    @SerializedName("job") val job: String,
    @SerializedName("name") val name: String
)

data class DetailsItem(
    val isAnActor: Boolean,
    val id: Int,
    val imagePath: String?,
    val title: String,
    val subtitle: String = ""
)

/*
data class MovieCrew(
    val adult: Boolean,
    val credit_id: String,
    val department: String,
    val gender: Int,
    val id: Int,
    val job: String,
    val known_for_department: String,
    val name: String,
    val original_name: String,
    val popularity: Double,
    val profile_path: String
)

data class MovieDetails(
    val adult: Boolean,
    val backdrop_path: String,
    val belongs_to_collection: Any,
    val budget: Int,
    val genres: List<Genre>,
    val homepage: String,
    val id: Int,
    val imdb_id: String,
    val original_language: String,
    val original_title: String,
    val overview: String,
    val popularity: Double,
    val poster_path: String,
    val production_companies: List<ProductionCompany>,
    val production_countries: List<ProductionCountry>,
    val release_date: String,
    val revenue: Int,
    val runtime: Int,
    val spoken_languages: List<SpokenLanguage>,
    val status: String,
    val tagline: String,
    val title: String,
    val video: Boolean,
    val vote_average: Double,
    val vote_count: Int
)

data class MovieActor(
    val adult: Boolean,
    val cast_id: Int,
    val character: String,
    val credit_id: String,
    val gender: Int,
    val id: Int,
    val known_for_department: String,
    val name: String,
    val order: Int,
    val original_name: String,
    val popularity: Double,
    val profile_path: String
)
*/