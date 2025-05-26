package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson

const val SEARCH_HISTORY = "search_history"

class SearchHistory(private val prefs: SharedPreferences) {
    private val gson = Gson()
    private val tracksHistory = mutableListOf<Track>()

    init {
        loadHistoryFromPrefs()
    }

    fun getTrackHistoryList(): MutableList<Track> {
        return tracksHistory
    }

    fun clearHistory() {
        tracksHistory.clear()
        saveHistoryToPrefs()
    }

    fun addToHistory(track: Track) {
        removeTrackIfExists(track)
        tracksHistory.add(0, track)
        if (tracksHistory.size > MAX_HISTORY_SIZE) {
            tracksHistory.removeAt(tracksHistory.lastIndex)
        }
        saveHistoryToPrefs()
    }

    private fun loadHistoryFromPrefs() {
        val json = prefs.getString(SEARCH_HISTORY, null)
        val arrayFromPrefs = gson.fromJson(json, Array<Track>::class.java) ?: emptyArray()
        tracksHistory.addAll(arrayFromPrefs)
    }

    private fun removeTrackIfExists(track: Track) {
        val iterator = tracksHistory.iterator()
        while (iterator.hasNext()) {
            val currentTrack = iterator.next()
            if (currentTrack.trackId == track.trackId) {
                iterator.remove()
            }
        }
    }

    fun saveHistoryToPrefs() {
        val json = gson.toJson(tracksHistory.take(MAX_HISTORY_SIZE)).toString()
        prefs.edit().putString(SEARCH_HISTORY, json).apply()
    }

    companion object {
        const val MAX_HISTORY_SIZE = 10
    }
}