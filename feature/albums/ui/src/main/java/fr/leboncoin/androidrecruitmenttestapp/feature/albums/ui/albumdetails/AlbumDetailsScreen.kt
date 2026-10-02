package fr.leboncoin.androidrecruitmenttestapp.feature.albums.ui.albumdetails

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.adevinta.spark.components.buttons.ButtonFilled
import com.adevinta.spark.components.progress.Spinner
import com.adevinta.spark.components.tags.TagTinted
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.ui.AlbumViewState

/**
 * This composable defines album details screen.
 *
 * @author alexandre.viravout
 */
@Composable
fun AlbumDetailsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AlbumDetailsViewModel = hiltViewModel()
) {
    BackHandler { onBack() }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is AlbumDetailsUiState.Loading -> AlbumDetailsLoadingView(modifier = modifier)
        is AlbumDetailsUiState.Success -> AlbumDetailsView(
            album = state.album,
            modifier = modifier,
        )

        is AlbumDetailsUiState.Error -> AlbumDetailsErrorView(
            message = state.message,
            onButtonClick = onBack,
            modifier = modifier,
        )
    }
}

@Composable
internal fun AlbumDetailsLoadingView(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Spinner()
    }
}

@Composable
internal fun AlbumDetailsErrorView(
    message: String,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = message)
        ButtonFilled(
            modifier = Modifier.padding(top = 16.dp),
            onClick = onButtonClick,
        ) {
            Text(text = "Ok")
        }
    }
}

@Composable
internal fun AlbumDetailsView(
    album: AlbumViewState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(state = rememberScrollState()),
    ) {
        AsyncImage(
            model = album.url,
            contentDescription = album.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(color = MaterialTheme.colorScheme.surfaceVariant),
        )

        Column(
            modifier = Modifier.padding(all = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = album.title,
                style = MaterialTheme.typography.headlineSmall,
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                TagTinted(text = "Album #${album.albumId}")
                TagTinted(text = "Photo #${album.id}")
            }
        }
    }
}