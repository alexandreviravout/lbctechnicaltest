package fr.leboncoin.androidrecruitmenttestapp.ui.albums

import fr.leboncoin.androidrecruitmenttestapp.ui.AlbumViewState

/**
 * This class defines ui state of the albums screen.
 *
 * @author alexandre.viravout
 */

sealed interface AlbumsUiState {
    data object Loading : AlbumsUiState
    data class Success(val albums: List<AlbumViewState>) : AlbumsUiState
    data class Error(val message: String) : AlbumsUiState
}