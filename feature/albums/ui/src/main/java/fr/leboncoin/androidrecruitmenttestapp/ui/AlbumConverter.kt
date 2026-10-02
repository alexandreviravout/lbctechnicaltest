package fr.leboncoin.androidrecruitmenttestapp.ui

import fr.leboncoin.androidrecruitmenttestapp.domain.model.Album
import javax.inject.Inject

/**
 * This class defines album converter.
 * Domain object is converted to view state object
 * in order to simple use it in composable.
 *
 * @author alexandre.viravout
 */
class AlbumConverter @Inject constructor() {

    fun convertToViewState(albums: List<Album>): List<AlbumViewState> =
        albums.map {
            convertToViewState(album = it)
        }

    fun convertToViewState(album: Album): AlbumViewState =
        AlbumViewState(
            id = album.id,
            albumId = album.albumId,
            title = album.title,
            url = album.url,
            thumbnailUrl = album.thumbnailUrl,
        )
}