package com.example.playlistmaker.mediateka.favorites.domain.impl

import com.example.playlistmaker.mediateka.favorites.domain.api.TracksDbInteractor
import com.example.playlistmaker.mediateka.favorites.domain.api.TracksDbRepository
import com.example.playlistmaker.search.domain.converters.TrackConverter.convertToDbEntity
import com.example.playlistmaker.search.domain.entity.Track
import kotlinx.coroutines.flow.Flow

class TracksDbInteractorImpl(val repository: TracksDbRepository) : TracksDbInteractor {
    override suspend fun addToDataBase(track: Track) {
        repository.addToDataBase(convertToDbEntity(track))
    }

    override suspend fun deleteFromFavorites(track: Track) {
        repository.deleteFromFavorites(convertToDbEntity(track))
    }

    override fun getFavorites(): Flow<List<Track>> {
        return repository.getFavorites()
    }
}