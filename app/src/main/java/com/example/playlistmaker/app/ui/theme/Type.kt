package com.example.playlistmaker.app.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.W400
import androidx.compose.ui.unit.sp


val Typography = CustomTypography(
    // region Экран поиска
    title = TextStyle(
        fontFamily = ysDisplayFontFamily,
        fontSize = 22.sp,
        fontWeight = FontWeight.W500
    ),
    searchText = TextStyle(
        fontFamily = ysDisplayFontFamily,
        fontSize = 16.sp,
        fontWeight = W400
    ),
    trackListUpperText = TextStyle(
        fontFamily = ysDisplayFontFamily,
        fontSize = 16.sp,
        fontWeight = W400
    ),
    trackListLowerText = TextStyle(
        fontFamily = ysDisplayFontFamily,
        fontSize = 11.sp,
        fontWeight = W400
    ),
    placeHolderText = TextStyle(
        fontFamily = ysDisplayFontFamily,
        fontSize = 19.sp,
        fontWeight = FontWeight.W500
    ),
    buttonText = TextStyle(
        fontFamily = ysDisplayFontFamily,
        fontSize = 14.sp,
        fontWeight = FontWeight.W500
    ),
    // endregion
    // Экран настроек
    settingsMenuItemText = TextStyle(
        fontFamily = ysDisplayFontFamily,
        fontSize = 16.sp,
        fontWeight = W400
    )
)

data class CustomTypography(
    // Экран поиска
    val title: TextStyle,
    val searchText: TextStyle,
    val trackListUpperText: TextStyle,
    val trackListLowerText: TextStyle,
    val placeHolderText: TextStyle,
    val buttonText: TextStyle,
    // Экран настроек
    val settingsMenuItemText: TextStyle
)