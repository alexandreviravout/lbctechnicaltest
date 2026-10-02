package fr.leboncoin.androidrecruitmenttestapp.feature.albums.data.local

import fr.leboncoin.androidrecruitmenttestapp.feature.albums.data.local.dao.AlbumDao
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
) {
    fun observeAlbums(): Flow<List<AlbumEntity>> = albumDao.observeAlbums()

    fun observeAlbum(id: Int): Flow<AlbumEntity?> = albumDao.observeAlbum(id)

    suspend fun saveAlbums(albums: List<AlbumEntity>) = albumDao.replaceAll(albums)
}