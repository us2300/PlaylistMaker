package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.data.impl.DelayHandlerImpl
import com.example.playlistmaker.domain.api.TrackSearchInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.consumer.TrackConsumer
import com.example.playlistmaker.util.Creator
import java.util.concurrent.Executors

const val SEARCH_DEBOUNCE_DELAY = 2000L

class TrackSearchInteractorImpl(private val repository: TracksRepository) : TrackSearchInteractor {

    private val executor = Executors.newSingleThreadExecutor()
    private val delay = DelayHandlerImpl(Creator.provideMainHandler())

    override fun searchTracks(expression: String, consumer: TrackConsumer) {
        executeSearchInNewThread(expression, consumer)
    }

    override fun searchTracksDebounce(expression: String, consumer: TrackConsumer) {
        delay.postDelayed(SEARCH_DEBOUNCE_DELAY) { executeSearchInNewThread(expression, consumer) }
    }

    override fun removeSearchCallbacks() {
        delay.stop()
    }

    private fun executeSearchInNewThread(expression: String, consumer: TrackConsumer) {
        executor.execute {
            val result = repository.searchTracks(expression)
            consumer.consume(result)
        }
    }
}