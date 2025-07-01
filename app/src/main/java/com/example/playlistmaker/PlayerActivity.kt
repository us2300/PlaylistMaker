package com.example.playlistmaker

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.Util.Companion.dpToPx
import com.example.playlistmaker.Util.Companion.getCoverArtwork512
import com.example.playlistmaker.Util.Companion.millisToMmSs
import com.google.android.material.appbar.MaterialToolbar

class PlayerActivity : AppCompatActivity() {
    private val player = MediaPlayer()
    private var playerState = STATE_DEFAULT
    private val handler = Handler(Looper.getMainLooper())

    private var previewUrl: String? = null
    private lateinit var playButton: ImageButton
    private lateinit var listeningTimeText: TextView
    private val timeRefreshRunnable = object : Runnable {
        override fun run() {
            if (playerState == STATE_PLAYING) {
                listeningTimeText.text = millisToMmSs(player.currentPosition)
                handler.postDelayed(this, TIME_REFRESH_DELAY)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val toolbar: MaterialToolbar = findViewById(R.id.player_toolbar)
        listeningTimeText = findViewById(R.id.listening_time)
        playButton = findViewById(R.id.play_button)

        val albumCover: ImageView = findViewById(R.id.album_cover)
        val trackName: TextView = findViewById(R.id.track_name)
        val artistName: TextView = findViewById(R.id.artist_name)
        val trackTime: TextView = findViewById(R.id.track_time)
        val collectionName:TextView = findViewById(R.id.album_name)
        val releaseDate: TextView = findViewById(R.id.year_value)
        val primaryGenreName: TextView = findViewById(R.id.genre)
        val country: TextView = findViewById(R.id.country)

        toolbar.setNavigationOnClickListener { finish() }

        // Секция с информацией о треке
        trackName.text = intent.getStringExtra("track_name")
        artistName.text = intent.getStringExtra("artist_name")
        trackTime.text = millisToMmSs(intent.getIntExtra("track_time_millis", 0))
        releaseDate.text = intent.getStringExtra("release_date")?.take(4)
        primaryGenreName.text = intent.getStringExtra("primary_genre_name")
        country.text = intent.getStringExtra("country")

        listeningTimeText.text = millisToMmSs(LISTENING_TIME_DEFAULT)
        previewUrl = intent.getStringExtra("preview_url")

        preparePlayer()

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

        playButton.setOnClickListener {
            when (playerState) {
                STATE_PREPARED, STATE_PAUSED -> startPlayer()
                STATE_PLAYING -> pausePlayer()
                else -> Toast.makeText(this, getString(R.string.error_player_not_prepared), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(timeRefreshRunnable)
        player.release()
    }

    private fun preparePlayer() {
        if (previewUrl == null) {
            Toast.makeText(this, getString(R.string.error_missing_link_to_track_preview), Toast.LENGTH_SHORT).show()
            return
        }
        player.setDataSource(previewUrl)
        player.prepareAsync()
        player.setOnPreparedListener {
            playerState = STATE_PREPARED
        }
        player.setOnCompletionListener {
            handler.removeCallbacks(timeRefreshRunnable)
            listeningTimeText.text = millisToMmSs(LISTENING_TIME_DEFAULT)
            playButton.setImageResource(R.drawable.button_play)
            playerState = STATE_PREPARED
        }
    }

    private fun startPlayer() {
        player.start()
        playerState = STATE_PLAYING
        handler.post(timeRefreshRunnable)
        playButton.setImageResource(R.drawable.button_pause)
    }

    private fun pausePlayer() {
        player.pause()
        playerState = STATE_PAUSED
        handler.removeCallbacks(timeRefreshRunnable)
        playButton.setImageResource(R.drawable.button_play)
    }

    companion object {
        const val STATE_DEFAULT: Byte = 0
        const val STATE_PREPARED: Byte = 1
        const val STATE_PLAYING: Byte = 2
        const val STATE_PAUSED: Byte = 3
        const val LISTENING_TIME_DEFAULT: Int = 0
        const val TIME_REFRESH_DELAY: Long = 300L
    }
}