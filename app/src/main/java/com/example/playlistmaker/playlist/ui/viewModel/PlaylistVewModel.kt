package com.example.playlistmaker.playlist.ui.viewModel

import androidx.lifecycle.ViewModel
import com.example.playlistmaker.mediateka.playlists.domain.api.PlaylistsInteractor
import com.example.playlistmaker.mediateka.playlists.domain.api.StorageInteractor
import com.example.playlistmaker.sharing.domain.api.SharingInteractor

class PlaylistVewModel(
    val storageInteractor: StorageInteractor,
    val playlistsInteractor: PlaylistsInteractor,
    val sharingInteractor: SharingInteractor
) : ViewModel() {
}