package com.example.playlistmaker.mediateka.playlists.domain.impl

import com.example.playlistmaker.mediateka.playlists.domain.api.PlaylistDbInteractor
import com.example.playlistmaker.mediateka.playlists.domain.api.PlaylistDbRepository
import com.example.playlistmaker.mediateka.playlists.domain.converter.PlaylistConverter
import com.example.playlistmaker.mediateka.playlists.domain.converter.PlaylistConverter.toPlaylistEntity
import com.example.playlistmaker.mediateka.playlists.domain.entity.Playlist
import com.example.playlistmaker.search.domain.converters.TrackConverter
import com.example.playlistmaker.search.domain.entity.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistDbInteractorImpl(private val repository: PlaylistDbRepository) :
    PlaylistDbInteractor {
    override suspend fun createPlaylist(playlist: Playlist) {
        repository.createPlaylist(toPlaylistEntity(playlist))
    }

    override suspend fun addTrackToPlaylist(playlist: Playlist, track: Track): Boolean {
        return repository.addTrackToPlaylist(
            playlist.id,
            TrackConverter.convertToDbEntity(track)
        )
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return repository.getAllPlaylists().map { playlistEntityList ->
            playlistEntityList.map { entity ->
                PlaylistConverter.toPlaylist(entity)
            }
        }
    }
}
