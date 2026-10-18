package fr.leboncoin.androidrecruitmenttestapp.feature.albums.ui.albumdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.github.michaelbull.result.mapBoth
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.leboncoin.androidrecruitmenttestapp.core.analytics.AnalyticsHelper
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.domain.usecase.GetAlbumDetailsUseCase
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.domain.usecase.ObserveFavoriteIdsUseCase
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.domain.usecase.ToggleFavoriteUseCase
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.ui.AlbumConverter
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.ui.navigation.AlbumDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * This class defines view model of album details.
 * The loaded album is combined with favorite ids, so the favorite state is updated
 * as soon as it changes in database.
 *
 * @author alexandre.viravout
 */
@HiltViewModel
class AlbumDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getAlbumDetailsUseCase: GetAlbumDetailsUseCase,
    observeFavoriteIdsUseCase: ObserveFavoriteIdsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val converter: AlbumConverter,
    analyticsHelper: AnalyticsHelper,
) : ViewModel() {

    private val albumId: Int = savedStateHandle.toRoute<AlbumDetails>().albumId

    private val loadState = MutableStateFlow<AlbumDetailsUiState>(value = AlbumDetailsUiState.Loading)

    val uiState: StateFlow<AlbumDetailsUiState> =
        combine(loadState, observeFavoriteIdsUseCase()) { state, favoriteIds ->
            when (state) {
                is AlbumDetailsUiState.Success -> state.copy(
                    album = state.album.copy(isFavorite = albumId in favoriteIds),
                )
                else -> state
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = AlbumDetailsUiState.Loading,
        )

    init {
        analyticsHelper.trackScreenView(screenName = "Details")

        viewModelScope.launch {
            getAlbumDetailsUseCase(id = albumId).mapBoth(
                success = {
                    loadState.value = AlbumDetailsUiState.Success(
                        album = converter.convertToViewState(album = it)
                    )
                },
                failure = {
                    loadState.value = AlbumDetailsUiState.Error(
                        message = "An error occurred, retry later",
                    )
                }
            )
        }
    }

    fun onFavoriteClick() {
        viewModelScope.launch { toggleFavoriteUseCase(id = albumId) }
    }
}