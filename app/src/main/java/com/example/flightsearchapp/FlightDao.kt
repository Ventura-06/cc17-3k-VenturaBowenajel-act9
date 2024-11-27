package com.example.flightsearchapp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FlightDao {
    @Query("SELECT * FROM airport WHERE iata_code LIKE :query OR name LIKE :query ORDER BY passengers DESC")
    fun searchAirports(query: String): List<Airport>

    @Query("SELECT * FROM favorite")
    fun getFavoriteRoutes(): List<Favorite>

    @Insert
    fun saveFavorite(favorite: Favorite)
}

