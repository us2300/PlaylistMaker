package com.example.playlistmaker.search.ui.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.playlistmaker.R
import com.example.playlistmaker.app.ui.theme.LocalTypography
import com.example.playlistmaker.search.ui.entity.SearchState
import com.example.playlistmaker.search.ui.entity.SearchState.PlaceHolder.NetworkError

@Composable
fun SearchPlaceholder(state: SearchState.PlaceHolder, onReloadButtonClicked: () -> Unit) {

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier.padding(top = dimensionResource(R.dimen.search_placeholder_padding_top)),
            painter = painterResource(id = state.imageId),
            contentDescription = null
        )

        Text(
            modifier = Modifier
                .padding(
                    top = dimensionResource(R.dimen.search_placeholder_text_padding_top),
                    bottom = dimensionResource(R.dimen.search_placeholder_text_padding_bottom)
                )
                .padding(horizontal = dimensionResource(R.dimen.search_placeholder_text_padding_horizontal)),
            text = stringResource(state.textId),
            style = LocalTypography.current.placeHolderText,
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center
        )

        if (state is NetworkError) {
            val buttonText = stringResource(R.string.reload)
            CustomButton(
                text = buttonText,
                onClick = { onReloadButtonClicked() }
            )
        }
    }
}

@Preview
@Composable
fun SearchPlaceholderErrorPreview() {
    SearchPlaceholder(
        NetworkError(
            R.drawable.img_connection_issues,
            R.string.connection_issues_check_connection,
        ),
        onReloadButtonClicked = { }
    )
}

@Preview
@Composable
fun SearchPlaceholderNothingFoundPreview() {
    SearchPlaceholder(
        SearchState.PlaceHolder.NothingFound(
            R.drawable.img_nothing_found,
            R.string.nothing_found
        ),
        onReloadButtonClicked = {}
    )
}
