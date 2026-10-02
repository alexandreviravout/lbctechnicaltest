package fr.leboncoin.androidrecruitmenttestapp.feature.albums.ui.albumdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.github.michaelbull.result.mapBoth
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.leboncoin.androidrecruitmenttestapp.core.analytics.AnalyticsHelper
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.domain.usecase.GetAlbumDetailsUseCase
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.ui.AlbumConverter
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.ui.navigation.AlbumDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * This class defines view model of album details.
 *
 * @author alexandre.viravout
 */
@HiltViewModel
class AlbumDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    analyticsHelper: AnalyticsHelper,
    private val getAlbumDetailsUseCase: GetAlbumDetailsUseCase,
    private val converter: AlbumConverter,
) : ViewModel() {

    private val albumId: Int = savedStateHandle.toRoute<AlbumDetails>().albumId

    private val _uiState =
        MutableStateFlow<AlbumDetailsUiState>(value = AlbumDetailsUiState.Loading)
    val uiState: StateFlow<AlbumDetailsUiState> = _uiState.asStateFlow()

    init {
        analyticsHelper.trackScreenView(screenName = "Details")

        viewModelScope.launch {
            getAlbumDetailsUseCase(id = albumId).mapBoth(
                success = {
                    _uiState.value = AlbumDetailsUiState.Success(
                        album = converter.convertToViewState(album = it)
                    )
                },
                failure = {
                    _uiState.value = AlbumDetailsUiState.Error(
                        message = "An error occurred, retry later",
                    )
                }
            )
        }
    }
}