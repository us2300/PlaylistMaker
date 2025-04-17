package com.example.playlistmaker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

class SearchActivity : AppCompatActivity() {

    var searchSavedInput: String = INPUT_DEF

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val editText = findViewById<EditText>(R.id.edit_text_search)
        val toolbar = findViewById<MaterialToolbar>(R.id.search_toolbar)
        val clearButton = findViewById<ImageView>(R.id.clear_button)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        clearButton.setOnClickListener {
            editText.text.clear()
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                searchSavedInput = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {

            }
        }

        editText.addTextChangedListener(textWatcher)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SAVED_INPUT, searchSavedInput)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchSavedInput = savedInstanceState.getString(SAVED_INPUT, INPUT_DEF)

        val editText = findViewById<EditText>(R.id.edit_text_search)
        editText.setText(searchSavedInput)
    }

    companion object {
        const val SAVED_INPUT = "SAVED_INPUT"
        const val INPUT_DEF = ""
    }
}