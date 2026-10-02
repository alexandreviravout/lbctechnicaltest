package fr.leboncoin.androidrecruitmenttestapp.domain.model

/**
 * This class define Album domain object.
 *
 * @author alexandre.viravout
 */
data class Album(
    val id: Int,
    val albumId: Int,
    val title: String,
    val url: String,
    val thumbnailUrl: String
)