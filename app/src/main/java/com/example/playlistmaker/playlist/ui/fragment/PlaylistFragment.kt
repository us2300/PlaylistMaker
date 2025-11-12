package com.example.playlistmaker.playlist.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.mediateka.playlists.domain.entity.Playlist
import com.example.playlistmaker.player.ui.fragment.PlayerFragment
import com.example.playlistmaker.playlist.ui.entity.PlaylistBottomSheetState
import com.example.playlistmaker.playlist.ui.viewModel.PlaylistVewModel
import com.example.playlistmaker.search.domain.entity.Track
import com.example.playlistmaker.search.ui.fragment.TrackAdapter
import com.example.playlistmaker.util.Util
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.android.ext.android.getKoin
import org.koin.core.parameter.parametersOf

class PlaylistFragment : Fragment() {

    private var binding: FragmentPlaylistBinding? = null
    private lateinit var viewModel: PlaylistVewModel
    private lateinit var adapter: TrackAdapter
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var playlist: Playlist

    private val playlistId by lazy {
        requireArguments().getInt(ARGS_PLAYLIST_ID)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = getKoin().get { parametersOf(playlistId) }
        binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.playlistToolbar?.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        viewModel.observePlaylist().observe(viewLifecycleOwner) {
            this.playlist = it

            Glide.with(this)
                .load(playlist.coverUri)
                .placeholder(R.drawable.album_placeholder)
                .centerCrop()
                .into(binding!!.playlistCover)

            binding?.apply {
                playlistTitle.text = playlist.title

                if (playlist.description == null) {
                    playlistDescription.isGone = true
                } else {
                    playlistDescription.isVisible = true
                    playlistDescription.text = playlist.description
                }
            }
            updateTextFields()
        }

        bottomSheetBehavior = BottomSheetBehavior.from(binding!!.playlistBottomSheet)

        bottomSheetBehavior.apply {
            isHideable = false
            isFitToContents = false
        }

        setupRecyclerView()

        viewModel.observeBottomSheetState().observe(viewLifecycleOwner) { state ->
            renderBottomSheetState(state)
        }
        viewModel.observeTracks().observe(viewLifecycleOwner) { tracks ->
            adapter.updateTrackList(tracks)
        }
    }

    private fun renderBottomSheetState(state: PlaylistBottomSheetState) {
        when (state) {
            PlaylistBottomSheetState.Hidden -> {
                bottomSheetBehavior.isHideable = true
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }

            is PlaylistBottomSheetState.Visible -> {
                binding?.root?.post {
                    bottomSheetBehavior.isHideable = false
                    setupBottomSheetPeekHeight()
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                }
            }
        }
    }

    private fun updateTextFields() {
        binding?.apply {
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

    private fun setupRecyclerView() {
        adapter = TrackAdapter(
            onItemClicked = { currentTrack ->
                findNavController().navigate(
                    R.id.playerFragment,
                    bundleOf(PlayerFragment.ARGS_TRACK to currentTrack)
                )
            },
            onItemLongClicked = { currentTrack ->
                showConfirmDeleteTrackDialog(currentTrack)
            }
        )
        binding?.apply {
            playlistRecyclerView.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            playlistRecyclerView.adapter = adapter
        }
    }

    private fun showConfirmDeleteTrackDialog(track: Track) {
        MaterialAlertDialogBuilder(requireContext(), R.style.CustomDialog)
            .setTitle(R.string.confirm_want_to_delete_track)
            .setPositiveButton(R.string.yes) { _, _ ->
                viewModel.onDeleteTrack(track)
            }
            .setNegativeButton(R.string.no) { _, _ -> }.show()
    }

    // Вычисляем высоту для bottom_sheet, чтобы не перекрывались кнопки и тулбар
    private fun setupBottomSheetPeekHeight() {
        val totalScreenHeight = binding!!.root.height
        val toolbarHeight = binding!!.playlistToolbar.height
        val buttonsBottomPosition = binding!!.playlistShareButton.bottom

        val marginTop = resources.getDimensionPixelSize(R.dimen.playlist_bottom_sheet_margin_top)
        val peekHeight = totalScreenHeight - buttonsBottomPosition - marginTop

        bottomSheetBehavior.peekHeight = peekHeight
        bottomSheetBehavior.halfExpandedRatio = peekHeight.toFloat() / totalScreenHeight
        bottomSheetBehavior.expandedOffset = toolbarHeight
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object {
        const val ARGS_PLAYLIST_ID = "playlist_id"
    }
}