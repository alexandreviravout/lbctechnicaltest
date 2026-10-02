package fr.leboncoin.androidrecruitmenttestapp.feature.albums.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.data.local.entity.AlbumEntity

/**
 * This class defines album dao for database.
 *
 * @author alexandre.viravout
 */
@Dao
interface AlbumDao {

    @Query("SELECT * FROM albums ORDER BY id")
    suspend fun getAlbums(): List<AlbumEntity>

    @Query("SELECT * FROM albums WHERE id = :id")
    suspend fun getAlbum(id: Int): AlbumEntity?

    @Upsert
    suspend fun upsertAll(albums: List<AlbumEntity>)

    @Query("DELETE FROM albums")
    suspend fun deleteAll()

    @Transaction
    suspend fun replaceAll(albums: List<AlbumEntity>) {
        deleteAll()
        upsertAll(albums)
    }
}