package fr.leboncoin.androidrecruitmenttestapp.ui.navigation

import kotlinx.serialization.Serializable

/**
 * This file defines all destinations available in albums navigation.
 *
 * @author alexandre.viravout
 */

@Serializable
data object AlbumList

@Serializable
data class AlbumDetails(val albumId: Int)