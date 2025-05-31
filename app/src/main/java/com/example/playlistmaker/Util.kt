package com.example.playlistmaker

import android.content.Context
import android.util.TypedValue
import java.text.SimpleDateFormat
import java.util.Locale

class Util {
    companion object {
        fun millisToMin(millis: Int) : String {
            return SimpleDateFormat("mm:ss", Locale.getDefault()).format(millis)
        }

        fun dpToPx(dp: Float, context: Context): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.resources.displayMetrics).toInt()
        }

        fun getCoverArtwork512(url100: String?) : String? {
            return url100?.replaceAfterLast('/',"512x512bb.jpg")
        }
    }
}