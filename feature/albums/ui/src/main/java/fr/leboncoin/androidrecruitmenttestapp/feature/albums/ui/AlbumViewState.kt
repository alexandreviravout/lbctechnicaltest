package fr.leboncoin.androidrecruitmenttestapp.feature.albums.ui

import kotlinx.serialization.Serializable

/**
 * This data class defines view state of an album.
 *
 * @author alexandre.viravout
 */
@Serializable
data class AlbumViewState(
    val id: Int = 0,
    val albumId: Int = 0,
    val title: String = "",
    val url: String = "",
    val thumbnailUrl: String = "",
)