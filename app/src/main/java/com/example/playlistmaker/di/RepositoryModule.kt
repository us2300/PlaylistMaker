package com.example.playlistmaker.di

import com.example.playlistmaker.player.data.impl.AudioPlayerRepositoryImpl
import com.example.playlistmaker.player.domain.api.AudioPlayerRepository
import com.example.playlistmaker.search.data.impl.SearchHistoryRepositoryImpl
import com.example.playlistmaker.mediateka.favorites.data.impl.TracksDbRepositoryImpl
import com.example.playlistmaker.search.data.impl.TrackSearchRepositoryImpl
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.mediateka.favorites.domain.api.TracksDbRepository
import com.example.playlistmaker.mediateka.playlists.data.impl.PlaylistDbRepositoryImpl
import com.example.playlistmaker.mediateka.playlists.data.impl.StorageRepositoryImpl
import com.example.playlistmaker.mediateka.playlists.domain.api.PlaylistDbRepository
import com.example.playlistmaker.mediateka.playlists.domain.api.StorageRepository
import com.example.playlistmaker.search.domain.api.TrackSearchRepository
import com.example.playlistmaker.settings.data.impl.ThemeRepositoryImpl
import com.example.playlistmaker.settings.domain.api.ThemeRepository
import com.example.playlistmaker.sharing.data.impl.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.data.impl.StringResourceProviderImpl
import com.example.playlistmaker.sharing.domain.api.ExternalNavigator
import com.example.playlistmaker.sharing.domain.api.StringResourceProvider
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {

    single<ThemeRepository> {
        ThemeRepositoryImpl(
            prefs = get(),
            context = androidContext()
        )
    }

    single<ExternalNavigator> {
        ExternalNavigatorImpl(
            context = androidContext()
        )
    }

    single<StringResourceProvider> {
        StringResourceProviderImpl(
            context = androidContext()
        )
    }

    single<TrackSearchRepository> {
        TrackSearchRepositoryImpl(
            networkClient = get(),
            dataBase = get()
        )
    }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(
            prefs = get(),
            gson = get(),
            dataBase = get()
        )
    }

    factory<AudioPlayerRepository> {
        AudioPlayerRepositoryImpl()
    }

    factory<TracksDbRepository> {
        TracksDbRepositoryImpl(
            dataBase = get()
        )
    }

    factory<PlaylistDbRepository> {
        PlaylistDbRepositoryImpl(
            dataBase = get()
        )
    }

    factory<StorageRepository> {
        StorageRepositoryImpl(
            context = androidContext()
        )
    }
}
