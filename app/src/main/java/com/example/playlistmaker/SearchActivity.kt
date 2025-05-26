package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
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
    private val searchResultsList = mutableListOf<Track>()
    private lateinit var searchHistoryList: MutableList<Track>

    private lateinit var searchResultsAdapter: TrackAdapter
    private lateinit var searchHistoryAdapter: TrackAdapter
    private lateinit var searchPlaceholderImg: ImageView
    private lateinit var searchPlaceholderText: TextView
    private lateinit var searchPlaceHolderButton: Button
    private lateinit var searchHistoryClearButton: Button
    private lateinit var searchHistoryView: LinearLayout
    private lateinit var searchHistoryRecyclerView: RecyclerView
    private lateinit var searchInput: EditText
    private lateinit var searchResultsRecyclerView: RecyclerView
    private lateinit var searchHistory: SearchHistory

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val toolbar: MaterialToolbar = findViewById(R.id.search_toolbar)
        val clearEditTextButton: ImageView = findViewById(R.id.search_edit_text_clear_button)

        searchInput = findViewById(R.id.edit_text_search)
        searchResultsRecyclerView = findViewById(R.id.search_results_recycler_view)

        searchPlaceholderImg = findViewById(R.id.search_placeholder_image)
        searchPlaceholderText = findViewById(R.id.search_placeholder_text)
        searchPlaceHolderButton = findViewById(R.id.search_placeholder_button)

        searchHistoryView = findViewById(R.id.search_history_view_group)
        searchHistoryRecyclerView = findViewById(R.id.search_history_recycler_view)
        searchHistoryClearButton = findViewById(R.id.search_history_clear_button)
        searchHistory = SearchHistory((applicationContext as App).sharedPrefs)

        searchResultsRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        searchHistoryRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        searchHistoryList = searchHistory.getTrackHistoryList()
        searchHistoryAdapter = TrackAdapter(searchHistoryList, searchHistory, true)
        searchHistoryRecyclerView.adapter = searchHistoryAdapter

        searchResultsAdapter = TrackAdapter(searchResultsList, searchHistory, false)
        searchResultsAdapter.onItemClickListener = { track ->
            searchHistory.addToHistory(track)
            searchHistoryAdapter.notifyDataSetChanged()
        }
        searchResultsRecyclerView.adapter = searchResultsAdapter

        ViewCompat.setOnApplyWindowInsetsListener(searchHistoryClearButton) { view, insets ->
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())

            searchHistoryClearButton.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = imeInsets.bottom
            }
            insets
        }

        toolbar.setNavigationOnClickListener {
            finish()
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearEditTextButton.isVisible = !s.isNullOrEmpty()
                if (searchInput.hasFocus() && s?.isEmpty() == true) showSearchHistory() else hideSearchHistory()
                searchSavedInput = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        searchInput.addTextChangedListener(textWatcher)
        searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideSearchHistory()
                hideSearchPlaceholders()
                tracksSearch()
            }
            false
        }
        searchInput.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && searchInput.text.isEmpty()) {
                showSearchHistory()
            } else hideSearchHistory()
        }

        clearEditTextButton.setOnClickListener {
            searchInput.text.clear()
            searchResultsList.clear()
            searchResultsAdapter.notifyDataSetChanged()

            hideSearchPlaceholders()

            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            if (currentFocus != null) {
                inputMethodManager?.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            }
        }

        searchHistoryClearButton.setOnClickListener {
            searchHistory.clearHistory()
            searchHistoryAdapter.notifyDataSetChanged()
            hideSearchHistory()
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

    override fun onPause() {
        super.onPause()
        searchHistory.saveHistoryToPrefs()
    }

    private fun tracksSearch() {
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
                        searchResultsList.addAll(results)
                        searchResultsAdapter.notifyDataSetChanged()
                    }
                    if (searchResultsList.isEmpty()) {
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
                searchResultsList.clear()
                searchResultsAdapter.notifyDataSetChanged()

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
                    getString(R.string.connection_issues) + "\n\n" + getString(R.string.load_failed_check_connection)
                searchResultsList.clear()
                searchResultsAdapter.notifyDataSetChanged()

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

    private fun showSearchHistory() {
        if (searchHistoryList.isNotEmpty()) {
            searchHistoryView.isVisible = true
            searchHistoryClearButton.isVisible = true
        }
    }

    private fun hideSearchHistory() {
        searchHistoryView.isGone = true
        searchHistoryClearButton.isGone = true
    }

    private fun hideSearchPlaceholders() {
        searchPlaceholderImg.isGone = true
        searchPlaceholderText.isGone = true
        searchPlaceHolderButton.isGone = true
    }

    companion object {
        const val SAVED_INPUT = "SAVED_INPUT"
        const val INPUT_DEF = ""
        const val NOTHING_FOUND: Byte = 0
        const val CONNECTION_ISSUES: Byte = -1
    }
}