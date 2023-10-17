package com.example.mymoviesapp.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object APIClient {

    private const val IMAGE_PATH = "https://image.tmdb.org/t/p/"
    const val IMAGE_PATH_ORIGINAL = IMAGE_PATH + "original"
    const val IMAGE_PATH_W780 = IMAGE_PATH + "w780"
    const val IMAGE_PATH_W342 = IMAGE_PATH + "w342"
    const val IMAGE_PATH_W185 = IMAGE_PATH + "w185"

    const val API_KEY = "4143fbf98b1ea93b9a2e2f3e691f2bc1"

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org/3/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: APIService = retrofit.create(APIService::class.java)
}