package com.example.mymoviesapp.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FavoriteMovie(
    @PrimaryKey(autoGenerate = false)
    val id: Int,

    val title: String,

    val posterPath: String,

    var position: Int,

    @ColumnInfo(defaultValue = "true") var isInFavorite:Boolean
)
