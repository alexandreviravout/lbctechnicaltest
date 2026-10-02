package fr.leboncoin.androidrecruitmenttestapp.feature.albums.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.data.local.dao.AlbumDao
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.data.local.entity.AlbumEntity

/**
 * This class defines the albums database.
 *
 * @author alexanre.viravout
 */
@Database(
    entities = [AlbumEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class AlbumsDatabase : RoomDatabase() {
    abstract fun albumDao(): AlbumDao
}