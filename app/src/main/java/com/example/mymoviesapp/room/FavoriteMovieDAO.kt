package com.example.mymoviesapp.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface FavoriteMovieDAO {

    @Query("SELECT * FROM FavoriteMovie ORDER BY position ASC")
    suspend fun getFavoriteMovies():List<FavoriteMovie>

    @Insert
    suspend fun addMovieToFavorites(movie: FavoriteMovie)

    @Update
    suspend fun updateFavoriteMovies(movies:List<FavoriteMovie>)

    @Query("DELETE FROM FavoriteMovie WHERE id=:id")
    suspend fun removeMovieById(id:Int)

    @Query("DELETE FROM FavoriteMovie WHERE id IN (:ids)")
    suspend fun removeMoviesByIds(ids: List<Int>)

    @Query("UPDATE FavoriteMovie SET position = position - 1 WHERE position > :oldPosition")
    suspend fun subtractPositionAfterOldPosition(oldPosition:Int)

    @Query("SELECT EXISTS(SELECT id FROM FavoriteMovie WHERE id = :id)")
    suspend fun isMovieInFavorites(id:Int): Boolean

    @Query("SELECT COALESCE(MAX(position), 0) FROM FavoriteMovie")
    suspend fun getLastPosition():Int

    @Query("SELECT * FROM FavoriteMovie WHERE id = :id")
    suspend fun getFavoriteMovieFromId(id:Int):FavoriteMovie
}