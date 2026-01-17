package com.example.playlistmaker.mediateka.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.app.ui.theme.PlaylistMakerTheme
import com.example.playlistmaker.mediateka.ui.composable.MediatekaScreen
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class MediatekaComposeFragment() : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return ComposeView(requireContext()).apply {
            setContent {
                PlaylistMakerTheme {
                    var bottomNavHeightDp by remember { mutableStateOf(0.dp) }

                    LaunchedEffect(Unit) {

                        withContext(Dispatchers.Main) {
                            delay(100)
                            val bottomNav =
                                activity?.findViewById<BottomNavigationView>(R.id.bottom_nav_view)

                            bottomNavHeightDp = if (bottomNav != null && bottomNav.height > 0) {
                                (bottomNav.height / resources.displayMetrics.density).dp
                            } else {
                                48.dp // Значение по умолчанию
                            }
                        }
                    }

                    MediatekaScreen(findNavController(), bottomNavHeightDp)
                }
            }
        }
    }
}