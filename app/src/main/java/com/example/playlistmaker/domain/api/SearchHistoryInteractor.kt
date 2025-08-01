package com.example.playlistmaker.domain.api

interface SearchHistoryInteractor<Track> {

    fun saveTrackToHistory(track: Track)

    fun getHistoryList(): MutableList<Track>

    fun clearHistory()
}