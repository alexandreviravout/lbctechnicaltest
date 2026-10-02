package fr.leboncoin.androidrecruitmenttestapp.feature.albums.ui.albumdetails

import fr.leboncoin.androidrecruitmenttestapp.feature.albums.ui.AlbumViewState

/**
 * This class defines ui state of album details screen.
 *
 * @author alexandre.viravout
 */

sealed interface AlbumDetailsUiState {
    data object Loading : AlbumDetailsUiState
    data class Success(val album: AlbumViewState) : AlbumDetailsUiState
    data class Error(val message: String) : AlbumDetailsUiState
}