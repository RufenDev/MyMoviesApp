package com.example.mymoviesapp.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface APIService {

    @GET("genre/movie/list")
    suspend fun getMoviesGenres(
        @Query("api_key") key: String = APIClient.API_KEY,
        @Query("language") lan: String = "es"
    ): MoviesGenreResponse

    @GET("movie/popular")
    suspend fun getHomePopularMovies(
        @Query("api_key") key: String = APIClient.API_KEY
    ): MoviesResponse

    @GET("discover/movie")
    suspend fun getMoviesByGenre(
        @Query("with_genres") genreID: Int,
        @Query("page") page:Int = 1,
        @Query("api_key") api: String = APIClient.API_KEY
    ): MoviesResponse

    @GET("search/movie")
    suspend fun getSearchMovies(
        @Query("api_key") api: String = APIClient.API_KEY,
        @Query("query") title:String,
        @Query("page") page:Int = 1,
        @Query("include_adult") adults:Boolean = false
    ): MoviesResponse

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieID:Int,
        @Query("api_key") api: String = APIClient.API_KEY,
        @Query("language") lan:String = "es"
    ): MovieDetails

    @GET("movie/{movie_id}/credits")
    suspend fun getMovieCredits(
        @Path("movie_id") movieID:Int,
        @Query("api_key") api: String = APIClient.API_KEY,
        @Query("language") lan:String = "es"
    ): MovieCredits

    @GET("movie/{movie_id}/recommendations")
    suspend fun getRecommendedMovies(
        @Path("movie_id") movieID:Int,
        @Query("page") page:Int = 1,
        @Query("api_key") api: String = APIClient.API_KEY,
    ) : MoviesResponse
}