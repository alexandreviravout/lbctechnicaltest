package fr.leboncoin.androidrecruitmenttestapp.feature.albums.ui.albums

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.michaelbull.result.mapBoth
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.domain.usecase.GetAlbumsUseCase
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.domain.usecase.ObserveFavoriteIdsUseCase
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.domain.usecase.ToggleFavoriteUseCase
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.ui.AlbumConverter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * This class defines view model of albums list.
 * Loaded albums are combined with favorite ids, so favorites are updated
 * as soon as they change in database, without reloading albums.
 *
 * @author alexandre.viravout
 */
@HiltViewModel
class AlbumsViewModel @Inject constructor(
    private val getAlbumsUseCase: GetAlbumsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    observeFavoriteIdsUseCase: ObserveFavoriteIdsUseCase,
    private val converter: AlbumConverter,
) : ViewModel() {

    private val loadState = MutableStateFlow<AlbumsUiState>(value = AlbumsUiState.Loading)

    val uiState: StateFlow<AlbumsUiState> =
        combine(loadState, observeFavoriteIdsUseCase()) { state, favoriteIds ->
            when (state) {
                is AlbumsUiState.Success -> state.copy(
                    albums = state.albums.map { it.copy(isFavorite = it.id in favoriteIds) },
                )
                else -> state
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = AlbumsUiState.Loading,
        )

    init {
        loadAlbums()
    }

    fun onRetry() {
        loadState.value = AlbumsUiState.Loading
        loadAlbums()
    }

    fun onFavoriteClick(id: Int) {
        viewModelScope.launch { toggleFavoriteUseCase(id = id) }
    }

    private fun loadAlbums() {
        viewModelScope.launch {
            getAlbumsUseCase(forceRefresh = true).mapBoth(
                success = {
                    loadState.value = AlbumsUiState.Success(
                        albums = converter.convertToViewState(albums = it)
                    )
                },
                failure = {
                    loadState.value = AlbumsUiState.Error(
                        message = "An error occurred, click on the button to retry to load albums",
                    )
                }
            )
        }
    }
}