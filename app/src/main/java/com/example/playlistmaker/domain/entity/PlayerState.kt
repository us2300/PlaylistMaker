package com.example.playlistmaker.domain.entity

sealed class PlayerState {
    data object PLAYING : PlayerState()
    data object PAUSED : PlayerState()
    data object PREPARED: PlayerState()
    data object DEFAULT: PlayerState()
}