package com.example.playlistmaker.mediateka.playlists.ui.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.mediateka.playlists.domain.api.PlaylistDbInteractor
import com.example.playlistmaker.mediateka.playlists.domain.entity.Playlist
import com.example.playlistmaker.mediateka.playlists.ui.entity.PlaylistsState
import kotlinx.coroutines.launch

class PlaylistsViewModel(private val playlistDbInteractor: PlaylistDbInteractor) : ViewModel() {
    val playlists = mutableListOf<Playlist>()

    init {
        updatePlaylists()
    }

    private val _stateLiveData = MutableLiveData<PlaylistsState>()
    fun observeState(): LiveData<PlaylistsState> = _stateLiveData

    private fun updatePlaylists() {
        viewModelScope.launch {
            playlistDbInteractor.getAllPlaylists().collect { dbPlaylists ->
                playlists.clear()
                playlists.addAll(dbPlaylists)
                updateState()
            }
        }
    }

    private fun updateState() {
        if (playlists.isEmpty()) {
            _stateLiveData.value = PlaylistsState.Placeholder
        } else {
            _stateLiveData.value = PlaylistsState.Content(playlists)
        }
    }
}
