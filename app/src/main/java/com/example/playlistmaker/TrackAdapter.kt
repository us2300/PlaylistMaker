package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TrackAdapter(
    val tracks: MutableList<Track>,
    val searchHistory: SearchHistory,
    private val isHistoryAdapter: Boolean
) : RecyclerView.Adapter<TrackViewHolder>() {

    var onItemClickListener: ((Track) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.track_search_item, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val currentTrack = tracks[position]
        holder.bind(currentTrack)
        if (!isHistoryAdapter) {
            holder.itemView.setOnClickListener {
                onItemClickListener?.invoke(currentTrack)
                searchHistory.addToHistory(currentTrack)
            }
        }
    }

    override fun getItemCount(): Int = tracks.size
}