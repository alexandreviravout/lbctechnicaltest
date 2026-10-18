package fr.leboncoin.androidrecruitmenttestapp.feature.albums.data.local

import fr.leboncoin.androidrecruitmenttestapp.feature.albums.data.local.dao.AlbumDao
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.data.local.dao.FavoriteDao
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.data.local.entity.AlbumEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * This class defines local data source of album list.
 * It allows us to get albums list stored in local.
 *
 * @author alexandre.viravout
 */
class AlbumLocalDataSource @Inject constructor(
    private val albumDao: AlbumDao,
    private val favoriteDao: FavoriteDao,
) {
    suspend fun getAlbums(): List<AlbumEntity> = albumDao.getAlbums()

    suspend fun getAlbum(id: Int): AlbumEntity? = albumDao.getAlbum(id = id)

    suspend fun saveAlbums(albums: List<AlbumEntity>) = albumDao.replaceAll(albums = albums)

    fun observeFavoriteIds(): Flow<List<Int>> = favoriteDao.observeFavoriteIds()

    suspend fun toggleFavorite(id: Int): Boolean = favoriteDao.toggle(id = id)
}