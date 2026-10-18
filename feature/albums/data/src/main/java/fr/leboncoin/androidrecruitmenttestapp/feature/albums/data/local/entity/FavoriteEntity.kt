package fr.leboncoin.androidrecruitmenttestapp.feature.albums.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * This class defines a favorite album for database.
 * It is kept in its own table so that refreshing albums never removes favorites.
 *
 * @author alexandre.viravout
 */
@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val id: Int, // AlbumEntity.id
)