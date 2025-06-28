package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.WindowCompat
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

    private val handler = Handler(Looper.getMainLooper())

    private val iTunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesService = retrofit.create(ITunesApi::class.java)

    @SuppressLint("NotifyDataSetChanged")
    private val searchRunnable = Runnable {
        if (searchResultsList.isNotEmpty()) {
            searchResultsList.clear()
            searchResultsAdapter.notifyDataSetChanged()
        }
        tracksSearch()
    }

    private var searchSavedInput: String = INPUT_DEF
    private val searchResultsList = mutableListOf<Track>()
    private lateinit var searchHistoryList: MutableList<Track>

    private lateinit var searchResultsAdapter: TrackAdapter
    private lateinit var searchHistoryAdapter: TrackAdapter
    private lateinit var placeHolderImg: ImageView
    private lateinit var placeHolderText: TextView
    private lateinit var placeHolderButton: Button
    private lateinit var clearButton: Button
    private lateinit var searchHistoryView: LinearLayout
    private lateinit var searchHistoryRecyclerView: RecyclerView
    private lateinit var searchInput: EditText
    private lateinit var searchResultsRecyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var searchHistory: SearchHistory

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val toolbar: MaterialToolbar = findViewById(R.id.search_toolbar)
        val clearEditTextButton: ImageView = findViewById(R.id.search_edit_text_clear_button)

        progressBar = findViewById(R.id.progress_circular)

        searchInput = findViewById(R.id.edit_text_search)
        searchResultsRecyclerView = findViewById(R.id.search_results_recycler_view)

        placeHolderImg = findViewById(R.id.search_placeholder_image)
        placeHolderText = findViewById(R.id.search_placeholder_text)
        placeHolderButton = findViewById(R.id.search_placeholder_button)

        searchHistoryView = findViewById(R.id.search_history_view_group)
        searchHistoryRecyclerView = findViewById(R.id.search_history_recycler_view)
        clearButton = findViewById(R.id.search_history_clear_button)
        searchHistory = SearchHistory((applicationContext as App).sharedPrefs)

        searchResultsRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        searchHistoryRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        searchHistoryList = searchHistory.getTrackHistoryList()
        searchHistoryAdapter = TrackAdapter(searchHistoryList, searchHistory)
        searchHistoryAdapter.onItemClickListener = { track ->
            searchHistory.addToHistory(track)
        }
        searchHistoryRecyclerView.adapter = searchHistoryAdapter

        searchResultsAdapter = TrackAdapter(searchResultsList, searchHistory)
        searchResultsAdapter.onItemClickListener = { track ->
            searchHistory.addToHistory(track)
        }
        searchResultsRecyclerView.adapter = searchResultsAdapter

        toolbar.setNavigationOnClickListener {
            finish()
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearEditTextButton.isVisible = !s.isNullOrEmpty()
                if (searchInput.hasFocus()) {
                    if (s?.isEmpty() == true) {
                        searchResultsList.clear()
                        searchResultsAdapter.notifyDataSetChanged()
                        showSearchHistory()
                    }
                    else hideSearchHistory()
                }
                searchSavedInput = s.toString()
                searchDebounce()
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        searchInput.addTextChangedListener(textWatcher)
        searchInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchInput.text.isEmpty()) {
                showSearchHistory()
            } else hideSearchHistory()
        }

        clearEditTextButton.setOnClickListener {
            handler.removeCallbacks(searchRunnable)
            searchInput.text.clear()
            searchResultsList.clear()
            searchResultsAdapter.notifyDataSetChanged()


            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            if (currentFocus != null) {
                inputMethodManager?.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            }
            searchInput.clearFocus()
        }

        clearButton.setOnClickListener {
            searchHistory.clearHistory()
            searchHistoryAdapter.notifyDataSetChanged()
            hideSearchHistory()
        }

        placeHolderButton.setOnClickListener {
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

    override fun onPause() {
        super.onPause()
        searchHistory.saveHistoryToPrefs()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        searchHistoryAdapter.notifyDataSetChanged()
    }

    private fun tracksSearch() {
        hideSearchPlaceholders()
        showProgressBar()
        if (searchSavedInput.isEmpty()) {
            hideProgressBar()
            return
        }
        iTunesService.search(searchSavedInput).enqueue(object : Callback<TracksResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<TracksResponse>,
                response: Response<TracksResponse>
            ) {
                if (response.isSuccessful) {
                    searchResultsList.clear()
                    val results = response.body()?.results ?: emptyList()
                    if (results.isNotEmpty()) {
                        hideProgressBar()
                        searchResultsList.addAll(results)
                        searchResultsAdapter.notifyDataSetChanged()
                    }
                    if (searchResultsList.isEmpty()) {
                        hideProgressBar()
                        showSearchPlaceholder(NOTHING_FOUND, "")
                    }
                } else {
                    hideProgressBar()
                    showSearchPlaceholder(CONNECTION_ISSUES, "Response code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                showSearchPlaceholder(CONNECTION_ISSUES, t.message.toString())
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showSearchPlaceholder(reason: Byte, toastMessage: String) {
        hideSearchHistory()
        when (reason) {
            NOTHING_FOUND -> {
                searchResultsList.clear()
                searchResultsAdapter.notifyDataSetChanged()

                placeHolderImg.setImageDrawable(
                    AppCompatResources.getDrawable(
                        this,
                        R.drawable.img_nothing_found
                    )
                )
                placeHolderText.setText(R.string.nothing_found)
                placeHolderImg.isVisible = true
                placeHolderText.isVisible = true
            }

            CONNECTION_ISSUES -> {
                val message =
                    getString(R.string.connection_issues) + "\n\n" + getString(R.string.load_failed_check_connection)
                searchResultsList.clear()
                searchResultsAdapter.notifyDataSetChanged()

                placeHolderImg.setImageDrawable(
                    AppCompatResources.getDrawable(
                        this,
                        R.drawable.img_connection_issues
                    )
                )
                placeHolderText.text = message
                placeHolderImg.isVisible = true
                placeHolderText.isVisible = true
                placeHolderButton.isVisible = true
                if (toastMessage.isNotEmpty()) {
                    Toast.makeText(applicationContext, toastMessage, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun showSearchHistory() {
        if (searchHistoryList.isNotEmpty()) {
            hideSearchPlaceholders()
            searchHistoryView.isVisible = true
            clearButton.isVisible = true
        }
    }

    private fun hideSearchHistory() {
        searchHistoryView.isGone = true
        clearButton.isGone = true
    }

    private fun hideSearchPlaceholders() {
        placeHolderImg.isGone = true
        placeHolderText.isGone = true
        placeHolderButton.isGone = true
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun showProgressBar() {
        progressBar.isVisible = true
    }

    private fun hideProgressBar() {
        progressBar.isGone = true
    }

    companion object {
        const val SAVED_INPUT = "SAVED_INPUT"
        const val INPUT_DEF = ""
        const val NOTHING_FOUND: Byte = 0
        const val CONNECTION_ISSUES: Byte = -1
        const val SEARCH_DEBOUNCE_DELAY = 1000L
    }
}