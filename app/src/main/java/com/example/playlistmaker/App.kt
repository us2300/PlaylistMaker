package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES

const val APP_PREFERENCES = "playlist_maker_preferences"
const val DARK_THEME_ENABLED = "dark_theme_enabled"

class App : Application() {

    var darkTheme = false
    lateinit var sharedPrefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()

        sharedPrefs = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)
        darkTheme = sharedPrefs.getBoolean(
            DARK_THEME_ENABLED,
            AppCompatDelegate.getDefaultNightMode() == MODE_NIGHT_YES
        )
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        sharedPrefs.edit()
            .putBoolean(DARK_THEME_ENABLED, darkTheme)
            .apply()

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) MODE_NIGHT_YES else MODE_NIGHT_NO
        )
    }
}