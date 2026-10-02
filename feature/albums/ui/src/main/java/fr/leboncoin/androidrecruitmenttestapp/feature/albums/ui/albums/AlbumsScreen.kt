package fr.leboncoin.androidrecruitmenttestapp.feature.albums.ui.albums

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.adevinta.spark.components.buttons.ButtonFilled
import com.adevinta.spark.components.progress.Spinner
import com.adevinta.spark.components.scaffold.Scaffold
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.ui.AlbumViewState

@Composable
fun AlbumsScreen(
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AlbumsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is AlbumsUiState.Loading -> AlbumsLoadingView(modifier = modifier)
        is AlbumsUiState.Success -> AlbumsListView(
            albums = state.albums,
            onItemSelected = onItemSelected,
            modifier = modifier,
        )

        is AlbumsUiState.Error -> AlbumsErrorView(
            message = state.message,
            onRetry = viewModel::onRetry,
            modifier = modifier,
        )
    }
}

@Composable
internal fun AlbumsLoadingView(
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
internal fun AlbumsErrorView(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = message)
        ButtonFilled(
            modifier = Modifier.padding(top = 16.dp),
            onClick = onRetry,
        ) {
            Text(text = "Retry")
        }
    }
}

@Composable
internal fun AlbumsListView(
    albums: List<AlbumViewState>,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(modifier = modifier) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = it,
        ) {
            items(
                items = albums,
                key = { album -> album.id }
            ) { album ->
                AlbumItem(
                    album = album,
                    onItemSelected = onItemSelected,
                )
            }
        }
    }
}