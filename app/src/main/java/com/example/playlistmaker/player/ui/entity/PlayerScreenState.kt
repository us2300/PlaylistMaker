package com.example.playlistmaker.player.ui.entity

import com.example.playlistmaker.mediateka.playlists.domain.entity.Playlist
import com.example.playlistmaker.player.domain.entity.PlayerState

data class PlayerScreenState(
    private val currentPosition: String,
    private val playerState: PlayerState,
    private val isFavorite: Boolean,
    private val isPlayButtonShown: Boolean,
    private val isBottomSheetVisible: Boolean,
    private val playlists: List<Playlist>
) {

    fun getPlayerState(): PlayerState = playerState
    fun getCurrentPosition() : String = currentPosition
    fun isFavorite(): Boolean = isFavorite
    fun isPlayButtonShown(): Boolean = isPlayButtonShown
    fun isBottomSheetVisible(): Boolean = isBottomSheetVisible
    fun getPlaylists(): List<Playlist> = playlists

    companion object {
        val DEFAULT = PlayerScreenState(
            currentPosition = "00:00",
            playerState = PlayerState.DEFAULT,
            isFavorite = false,
            isPlayButtonShown = true,
            isBottomSheetVisible = false,
            playlists = emptyList()
        )
    }
}