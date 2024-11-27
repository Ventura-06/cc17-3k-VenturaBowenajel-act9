package com.example.flightsearchapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.room.Room


class FlightSearchViewModel(application: Application) : AndroidViewModel(application) {
    private val db = Room.databaseBuilder(
        application, FlightDatabase::class.java, "flight_search.db"
    ).createFromAsset("flight_search.db").build()

    val flights = MutableLiveData<List<Airport>>()
    val favorites = MutableLiveData<List<Favorite>>()

    fun searchAirports(query: String) {
        flights.value = db.flightDao().searchAirports("%$query%")
    }

    fun getFavorites() {
        favorites.value = db.flightDao().getFavoriteRoutes()
    }

    fun saveFavorite(favorite: Favorite) {
        db.flightDao().saveFavorite(favorite)
    }
}
