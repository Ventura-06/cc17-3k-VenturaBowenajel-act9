package com.example.flightsearchapp

import android.app.Application
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity

import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.reflect.Modifier

class MainActivity : AppCompatActivity() {

    private lateinit var searchField: TextInputEditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var database: FlightDatabase
    private lateinit var flightAdapter: FlightAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        searchField = findViewById(R.id.search_field)
        recyclerView = findViewById(R.id.recycler_view)


        preferencesManager = PreferencesManager(applicationContext)
        database = Room.databaseBuilder(
            applicationContext,
            FlightDatabase::class.java, "flight_search.db"
        ).createFromAsset("flight_search.db").build()


        flightAdapter = FlightAdapter { favorite ->
            saveFavoriteRoute(favorite)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = flightAdapter


        searchField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                val query = charSequence.toString()
                if (query.isNotBlank()) {
                    lifecycleScope.launch {
                        preferencesManager.saveSearchQuery(query)
                        searchFlights(query)
                    }
                } else {

                    lifecycleScope.launch {
                        showSavedFavorites()
                    }
                }
            }

            override fun afterTextChanged(editable: Editable?) {

            }
        })

        lifecycleScope.launch {
            val savedQuery = preferencesManager.getSearchQuery()
            if (!savedQuery.isNullOrEmpty()) {
                searchField.setText(savedQuery)
                searchFlights(savedQuery)
            } else {
                showSavedFavorites()
            }
        }
    }

    private suspend fun searchFlights(query: String) {
        val queryResults = withContext(Dispatchers.IO) {

            val departures = database.flightDao().searchAirports("%$query%")
            val destinations = database.flightDao().searchAirports("%")

            departures.flatMap { departure ->
                destinations.filter { it.id != departure.id }
                    .map { destination -> Pair(departure, destination) }
            }
        }

        if (queryResults.isEmpty()) {
            flightAdapter.submitList(emptyList())
        } else {
            flightAdapter.submitList(queryResults)
        }
    }


    private suspend fun showSavedFavorites() {
        val savedFavorites = withContext(Dispatchers.IO) {
            database.flightDao().getFavoriteRoutes()
        }

        if (savedFavorites.isEmpty()) {
            flightAdapter.submitList(emptyList())
        } else {
            val favoriteRoutes = savedFavorites.map { favorite ->
                val departure = withContext(Dispatchers.IO) {
                    database.flightDao().searchAirports("%${favorite.departureCode}%").first()
                }
                val destination = withContext(Dispatchers.IO) {
                    database.flightDao().searchAirports("%${favorite.destinationCode}%").first()
                }
                Pair(departure, destination)
            }
            flightAdapter.submitList(favoriteRoutes)
        }
    }

    private fun saveFavoriteRoute(favorite: Favorite) {
        lifecycleScope.launch(Dispatchers.IO) {
            database.flightDao().saveFavorite(favorite)
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, "Route saved as favorite!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}