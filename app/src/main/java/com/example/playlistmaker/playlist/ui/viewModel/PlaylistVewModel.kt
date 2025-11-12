package com.example.playlistmaker.playlist.ui.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.mediateka.playlists.domain.api.PlaylistsInteractor
import com.example.playlistmaker.mediateka.playlists.domain.api.StorageInteractor
import com.example.playlistmaker.mediateka.playlists.domain.entity.Playlist
import com.example.playlistmaker.playlist.ui.entity.PlaylistBottomSheetState
import com.example.playlistmaker.search.domain.entity.Track
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import kotlinx.coroutines.launch

class PlaylistVewModel(
    val playlistsInteractor: PlaylistsInteractor,
    val sharingInteractor: SharingInteractor,
    val playlistId: Int
) : ViewModel() {

    private val _bottomSheetState =
        MutableLiveData<PlaylistBottomSheetState>(PlaylistBottomSheetState.Hidden)

    private val _tracks = MutableLiveData<List<Track>>()

    private val _playlist = MutableLiveData<Playlist>()

    fun observeBottomSheetState(): LiveData<PlaylistBottomSheetState> = _bottomSheetState
    fun observeTracks(): LiveData<List<Track>> = _tracks
    fun observePlaylist(): LiveData<Playlist> = _playlist

    init {
        loadPlaylist()
        loadTracks()
    }

    private fun loadPlaylist() {
        viewModelScope.launch {
            val playlist = playlistsInteractor.getPlaylistById(playlistId).collect {
                _playlist.postValue(it)
            }
        }
    }

    private fun loadTracks() {
        viewModelScope.launch {
            playlistsInteractor.getTracksByPlaylistId(playlistId).collect { tracks ->
                updateBottomSheetState(tracks)
                _tracks.value = tracks
            }
        }
    }

    private fun updateBottomSheetState(tracks: List<Track>) {
        if (tracks.isNotEmpty()) {
            _bottomSheetState.value = PlaylistBottomSheetState.Visible
        } else {
            _bottomSheetState.value = PlaylistBottomSheetState.Hidden
        }
    }

    fun onDeleteTrack(track: Track) {
        viewModelScope.launch {
            playlistsInteractor.deleteTrackFromPlaylist(playlistId, track.trackId)
            loadPlaylist()
            loadTracks()
        }
    }
}