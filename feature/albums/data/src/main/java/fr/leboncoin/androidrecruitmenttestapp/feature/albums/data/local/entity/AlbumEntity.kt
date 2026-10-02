package fr.leboncoin.androidrecruitmenttestapp.feature.albums.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * This class defines album entity for database.
 *
 * @author alexandre.viravout
 */
@Entity(tableName = "albums")
data class AlbumEntity(
    @PrimaryKey val id: Int,
    val albumId: Int,
    val title: String,
    val url: String,
    val thumbnailUrl: String,
)