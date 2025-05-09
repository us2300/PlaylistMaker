package com.example.playlistmaker

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val trackName: TextView = itemView.findViewById(R.id.track_name)
    private val artistName: TextView = itemView.findViewById(R.id.artist_name)
    private val trackTime: TextView = itemView.findViewById(R.id.track_time)
    private val albumCover: ImageView = itemView.findViewById(R.id.album_cover)

    fun bind(model: Track) {
        trackName.text = model.trackName
        artistName.text = model.artistName
        trackTime.text = millisToMin(model.trackTimeMillis)
        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.album_placeholder)
            .centerCrop()
            .transform(RoundedCorners(dpToPx(2f, itemView.context)))
            .into(albumCover)
    }

    private fun millisToMin(millis: Int) : String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(millis)
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }

}