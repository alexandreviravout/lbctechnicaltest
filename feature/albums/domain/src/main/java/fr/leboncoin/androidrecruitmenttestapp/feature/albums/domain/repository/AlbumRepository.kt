package fr.leboncoin.androidrecruitmenttestapp.feature.albums.domain.repository

import com.github.michaelbull.result.Result
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.domain.model.Album
import kotlinx.coroutines.flow.Flow

/**
 * This interface defines album repository.
 * It allows to get albums and album details, and to manage favorite albums.
 * Albums are returned in Result object type: a value on success, or the error that occurred.
 *
 * @author alexandre.viravout
 */
interface AlbumRepository {
    suspend fun getAlbums(forceRefresh: Boolean = false): Result<List<Album>, Throwable>
    suspend fun getAlbumDetails(id: Int): Result<Album, Throwable>
    fun observeFavoriteIds(): Flow<Set<Int>>
    suspend fun toggleFavorite(id: Int): Result<Boolean, Throwable>
}