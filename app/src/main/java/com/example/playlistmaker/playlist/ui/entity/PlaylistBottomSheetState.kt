package com.example.playlistmaker.playlist.ui.entity

import com.example.playlistmaker.search.domain.entity.Track

sealed class PlaylistBottomSheetState {

    data object Hidden : PlaylistBottomSheetState()

    data object Visible : PlaylistBottomSheetState()
}
