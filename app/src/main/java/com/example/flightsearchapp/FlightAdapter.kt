package com.example.flightsearchapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView


class FlightAdapter(private val onSaveFavorite: (Favorite) -> Unit) :
    ListAdapter<Pair<Airport, Airport>, FlightAdapter.FlightViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlightViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.flight_item, parent, false)
        return FlightViewHolder(view)
    }

    override fun onBindViewHolder(holder: FlightViewHolder, position: Int) {
        val (departure, destination) = getItem(position)
        holder.bind(departure, destination)
    }

    inner class FlightViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val departureAirport: TextView = itemView.findViewById(R.id.departure_airport)
        private val destinationAirport: TextView = itemView.findViewById(R.id.destination_airport)
        private val saveButton: Button = itemView.findViewById(R.id.save_button)

        fun bind(departure: Airport, destination: Airport) {
            // Set text values
            departureAirport.text = "Departure: ${departure.iataCode} - ${departure.name}"
            destinationAirport.text = "Destination: ${destination.iataCode} - ${destination.name}"

            // Set save button click listener
            saveButton.setOnClickListener {
                val favorite = Favorite(
                    id = 0, // Auto-generated by Room
                    departureCode = departure.iataCode,
                    destinationCode = destination.iataCode
                )
                onSaveFavorite(favorite)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Pair<Airport, Airport>>() {
        override fun areItemsTheSame(
            oldItem: Pair<Airport, Airport>,
            newItem: Pair<Airport, Airport>
        ) = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: Pair<Airport, Airport>,
            newItem: Pair<Airport, Airport>
        ) = oldItem == newItem
    }
}
