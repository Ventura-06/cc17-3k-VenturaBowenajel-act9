package com.example.flightsearchapp

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit

import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

import androidx.datastore.preferences.core.stringPreferencesKey


private val Context.dataStore by preferencesDataStore(name = "settings")

class PreferencesManager(private val context: Context) {

    private val searchQueryKey = stringPreferencesKey("search_query")

    suspend fun saveSearchQuery(query: String) {
        context.dataStore.edit { preferences ->
            preferences[searchQueryKey] = query
        }
    }

    suspend fun getSearchQuery(): String? {
        val preferences = context.dataStore.data.first()
        return preferences[searchQueryKey]
    }
}

