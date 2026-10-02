package fr.leboncoin.androidrecruitmenttestapp.feature.albums.data.api.dto

import kotlinx.serialization.Serializable

/**
 * This data class defines album dto object.
 *
 * @author alexandre.viravout
 */
@Serializable
data class AlbumDto(
    val id: Int,
    val albumId: Int,
    val title: String,
    val url: String,
    val thumbnailUrl: String
)