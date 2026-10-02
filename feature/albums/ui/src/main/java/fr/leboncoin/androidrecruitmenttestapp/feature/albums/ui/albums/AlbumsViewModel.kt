package fr.leboncoin.androidrecruitmenttestapp.feature.albums.ui.albums

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.michaelbull.result.mapBoth
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.domain.usecase.GetAlbumsUseCase
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.ui.AlbumConverter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumsViewModel @Inject constructor(
    private val getAlbumsUseCase: GetAlbumsUseCase,
    private val converter: AlbumConverter,
) : ViewModel() {

    private val _uiState = MutableStateFlow<AlbumsUiState>(value = AlbumsUiState.Loading)
    val uiState: StateFlow<AlbumsUiState> = _uiState.asStateFlow()

    init {
        loadAlbums()
    }

    fun onRetry() {
        _uiState.value = AlbumsUiState.Loading
        loadAlbums()
    }

    private fun loadAlbums() {
        viewModelScope.launch {
            getAlbumsUseCase(forceRefresh = true).mapBoth(
                success = {
                    _uiState.value = AlbumsUiState.Success(
                        albums = converter.convertToViewState(albums = it)
                    )
                },
                failure = {
                    _uiState.value = AlbumsUiState.Error(
                        message = "An error occurred, click on the button to retry to load albums",
                    )
                }
            )
        }
    }
}