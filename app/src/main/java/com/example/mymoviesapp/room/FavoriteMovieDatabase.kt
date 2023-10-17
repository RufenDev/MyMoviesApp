package com.example.mymoviesapp.room

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [FavoriteMovie::class],
    version = 2,
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ]
)
abstract class FavoriteMovieDatabase : RoomDatabase() {

    abstract fun favoriteMovieDAO(): FavoriteMovieDAO

}