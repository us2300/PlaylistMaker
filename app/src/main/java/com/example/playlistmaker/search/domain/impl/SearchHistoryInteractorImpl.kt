package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.data.mapper.TrackListFromDtoMapper
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.search.domain.entity.Track
import com.example.playlistmaker.search.domain.converters.TrackConverter

class SearchHistoryInteractorImpl(private val repository: SearchHistoryRepository) :
    SearchHistoryInteractor {

    override fun saveTrackToHistory(track: Track) {
        val dto = TrackConverter.convertToDto(track)
        repository.saveTrackToHistory(dto)
    }

    override suspend fun getHistoryList(): MutableList<Track> {
        val dto = repository.getHistoryList()
        return TrackListFromDtoMapper.map(dto).toMutableList()
    }

    override fun clearHistory() {
        repository.clearHistory()
    }
}