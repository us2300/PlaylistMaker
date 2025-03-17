package com.example.playlistmaker

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchButton: Button = findViewById<Button>(R.id.mainMenuSearchButton)
        searchButton.setOnClickListener {
            Toast.makeText(this@MainActivity, "Нажата кнопка поиска", Toast.LENGTH_SHORT).show()
        }

        val mediaButton = findViewById<Button>(R.id.mainMenuMediaButton)
        val buttonClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(this@MainActivity, "Нажата кнопка меиатеки", Toast.LENGTH_SHORT).show()
            }
        }
        mediaButton.setOnClickListener(buttonClickListener)

        val settingsButton = findViewById<Button>(R.id.mainMenuSettingsButton)
        settingsButton.setOnClickListener {
            Toast.makeText(this@MainActivity, "Нажата кнопка настроек", Toast.LENGTH_SHORT).show()
        }
    }
}
