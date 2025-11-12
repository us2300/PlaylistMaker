package com.example.playlistmaker.playlist.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.mediateka.playlists.domain.entity.Playlist
import com.example.playlistmaker.playlist.ui.viewModel.PlaylistVewModel
import com.example.playlistmaker.util.Util
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class PlaylistFragment : Fragment() {

    private var binding: FragmentPlaylistBinding? = null
    private val viewModel: PlaylistVewModel by viewModel()

    private val playlist: Playlist by lazy {
        @Suppress("DEPRECATION")
        requireArguments().getParcelable<Playlist>(ARGS_PLAYLIST) as Playlist
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.playlistToolbar?.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        playlist.coverUri?.let { coverUri ->
            Glide.with(this)
                .load(coverUri)
                .placeholder(R.drawable.album_placeholder)
                .centerCrop()
                .into(binding!!.playlistCover)
        }

        binding?.apply {
            playlistTitle.text = playlist.title

            if (playlist.description == null) {
                playlistDescription.isGone = true
            } else {
                playlistDescription.isVisible = true
                playlistDescription.text = playlist.description
            }

            val totalTimeMillis = playlist.getTotalTracksDuration()
            val totalTrackTimeText = getString(
                R.string.minutes,
                totalTimeMillis,
                Util.getRusNumeralMinutesEnding(totalTimeMillis)
            )
            playlistTotalTrackTime.text = totalTrackTimeText

            val totalTracksCount = playlist.getTracksCount()
            val totalTracksCountText = getString(
                R.string.tracks,
                totalTracksCount,
                Util.getRusNumeralTrackEnding(totalTracksCount)
            )
            playlistTracksCount.text = totalTracksCountText
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object {
        const val ARGS_PLAYLIST = "playlist"
    }
}