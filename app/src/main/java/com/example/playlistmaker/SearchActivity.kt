package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private val iTunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesService = retrofit.create(ITunesApi::class.java)

    private var searchSavedInput: String = INPUT_DEF
    private var tracksList = mutableListOf<Track>()
    private val tracksAdapter = TrackAdapter(tracksList)

    private lateinit var searchPlaceholderImg: ImageView
    private lateinit var searchPlaceholderText: TextView
    private lateinit var searchPlaceHolderButton: Button
    private lateinit var searchInput: EditText
    private lateinit var searchResults: RecyclerView

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val toolbar: MaterialToolbar = findViewById(R.id.search_toolbar)
        val clearButton: ImageView = findViewById(R.id.clear_button)
        searchInput = findViewById(R.id.edit_text_search)
        searchResults = findViewById(R.id.search_results)
        searchPlaceholderImg = findViewById(R.id.search_placeholder_image)
        searchPlaceholderText = findViewById(R.id.search_placeholder_text)
        searchPlaceHolderButton = findViewById(R.id.search_placeholder_button)

        searchResults.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        searchResults.adapter = tracksAdapter

        toolbar.setNavigationOnClickListener {
            finish()
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.isVisible = !s.isNullOrEmpty()
                searchSavedInput = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        searchInput.addTextChangedListener(textWatcher)
        searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideSearchPlaceholders()
                tracksSearch()
            }
            false
        }

        clearButton.setOnClickListener {
            searchInput.text.clear()
            tracksList.clear()
            tracksAdapter.notifyDataSetChanged()
            hideSearchPlaceholders()

            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            if (currentFocus != null) {
                inputMethodManager?.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            }
        }

        searchPlaceHolderButton.setOnClickListener {
            hideSearchPlaceholders()
            tracksSearch()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SAVED_INPUT, searchSavedInput)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchSavedInput = savedInstanceState.getString(SAVED_INPUT, INPUT_DEF)

        if (searchSavedInput.isNotEmpty()) {
            searchInput.setText(searchSavedInput)
            tracksSearch()
        }
    }

    private fun tracksSearch() {
        iTunesService.search(searchSavedInput).enqueue(object : Callback<TracksResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<TracksResponse>,
                response: Response<TracksResponse>
            ) {
                if (response.code() == 200) {
                    tracksList.clear()
                    if (response.body()?.results?.isNotEmpty() == true) {
                        tracksList.addAll(response.body()?.results!!)
                        tracksAdapter.notifyDataSetChanged()
                    }
                    if (tracksList.isEmpty()) {
                        showSearchPlaceholder(NOTHING_FOUND, "")
                    }
                } else {
                    showSearchPlaceholder(CONNECTION_ISSUES, "Response code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                showSearchPlaceholder(CONNECTION_ISSUES, t.message.toString())
            }
        })
        searchInput.clearFocus()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showSearchPlaceholder(reason: Byte, toastMessage: String) {
        when (reason) {
            NOTHING_FOUND -> {
                tracksList.clear()
                tracksAdapter.notifyDataSetChanged()

                searchPlaceholderImg.setImageDrawable(
                    AppCompatResources.getDrawable(
                        this,
                        R.drawable.img_nothing_found
                    )
                )
                searchPlaceholderText.setText(R.string.nothing_found)
                searchPlaceholderImg.isVisible = true
                searchPlaceholderText.isVisible = true
            }

            CONNECTION_ISSUES -> {
                val message =
                    "${getString(R.string.connection_issues)}\n\n${getString(R.string.load_failed_check_connection)}"
                tracksList.clear()
                tracksAdapter.notifyDataSetChanged()

                searchPlaceholderImg.setImageDrawable(
                    AppCompatResources.getDrawable(
                        this,
                        R.drawable.img_connection_issues
                    )
                )
                searchPlaceholderText.text = message
                searchPlaceholderImg.isVisible = true
                searchPlaceholderText.isVisible = true
                searchPlaceHolderButton.isVisible = true
                if (toastMessage.isNotEmpty()) {
                    Toast.makeText(applicationContext, toastMessage, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun hideSearchPlaceholders() {
        searchPlaceholderImg.isGone = true
        searchPlaceholderText.isGone = true
    }

    companion object {
        const val SAVED_INPUT = "SAVED_INPUT"
        const val INPUT_DEF = ""
        const val NOTHING_FOUND: Byte = 0
        const val CONNECTION_ISSUES: Byte = -1
    }
}