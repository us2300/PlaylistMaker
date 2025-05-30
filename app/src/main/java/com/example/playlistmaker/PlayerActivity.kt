package com.example.playlistmaker

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.Util.Companion.dpToPx
import com.example.playlistmaker.Util.Companion.getCoverArtwork512
import com.example.playlistmaker.Util.Companion.millisToMin
import com.google.android.material.appbar.MaterialToolbar

class PlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.activity_player)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        val toolbar: MaterialToolbar = findViewById(R.id.player_toolbar)

        val albumCover: ImageView = findViewById(R.id.album_cover)
        val trackName: TextView = findViewById(R.id.track_name)
        val artistName: TextView = findViewById(R.id.artist_name)
        val trackTime: TextView = findViewById(R.id.track_time)
        val collectionName:TextView = findViewById(R.id.album_name)
        val releaseDate: TextView = findViewById(R.id.year_value)
        val primaryGenreName: TextView = findViewById(R.id.genre)
        val country: TextView = findViewById(R.id.country)

        toolbar.setNavigationOnClickListener { finish() }

        trackName.text = intent.getStringExtra("track_name")
        artistName.text = intent.getStringExtra("artist_name")
        trackTime.text = millisToMin(intent.getIntExtra("track_time_millis", 0))
        releaseDate.text = intent.getStringExtra("release_date")?.take(4)
        primaryGenreName.text = intent.getStringExtra("primary_genre_name")
        country.text = intent.getStringExtra("country")

        val albumName = intent.getStringExtra("collection_name")
        if (albumName == null) {
            val albumNameText = findViewById<TextView>(R.id.album_text)
            albumNameText.isGone = true
            collectionName.isGone = true
        } else {
            collectionName.text = albumName
        }

        val albumCoverUrl100 = intent.getStringExtra("artwork_url_100")
        val artworkUrl512 = getCoverArtwork512(albumCoverUrl100)

        Glide.with(this)
            .load(artworkUrl512)
            .placeholder(R.drawable.album_placeholder)
            .centerCrop()
            .transform(RoundedCorners(dpToPx(8f, this)))
            .into(albumCover)
    }
}