package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
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
        if (!sharedPrefs.contains(DARK_THEME_ENABLED)) {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM)
            val isSystemDarkThemeEnabled =
                (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
            darkTheme = isSystemDarkThemeEnabled
        } else {
            darkTheme = sharedPrefs.getBoolean(
                DARK_THEME_ENABLED, false
            )
            AppCompatDelegate.setDefaultNightMode(
                if (darkTheme) MODE_NIGHT_YES else MODE_NIGHT_NO
            )
        }
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