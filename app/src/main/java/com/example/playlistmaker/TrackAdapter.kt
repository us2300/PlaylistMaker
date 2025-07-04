package com.example.playlistmaker

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TrackAdapter(
    private val tracks: MutableList<Track>,
    private val searchHistory: SearchHistory
) : RecyclerView.Adapter<TrackViewHolder>() {
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.track_search_item, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val currentTrack = tracks[position]
        holder.bind(currentTrack)
        holder.itemView.setOnClickListener {
            if (clickDebounce()) {
                searchHistory.addToHistory(currentTrack)
                val playerIntent =
                    Intent(holder.itemView.context, PlayerActivity::class.java).apply {
                        putExtra("track_name", currentTrack.trackName)
                        putExtra("artist_name", currentTrack.artistName)
                        putExtra("track_time_millis", currentTrack.trackTimeMillis)
                        putExtra("collection_name", currentTrack.collectionName)
                        putExtra("release_date", currentTrack.releaseDate)
                        putExtra("primary_genre_name", currentTrack.primaryGenreName)
                        putExtra("country", currentTrack.country)
                        putExtra("artwork_url_100", currentTrack.artworkUrl100)
                        putExtra("preview_url", currentTrack.previewUrl)
                    }
                holder.itemView.context.startActivity(playerIntent)
            }
        }
    }

    override fun getItemCount(): Int = tracks.size

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    companion object {
        const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}