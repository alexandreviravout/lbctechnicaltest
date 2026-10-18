package fr.leboncoin.androidrecruitmenttestapp.feature.albums.domain.repository

import com.github.michaelbull.result.Result
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.domain.model.Album
import kotlinx.coroutines.flow.Flow

/**
 * This interface defines album repository.
 * Its allows to get albums in Result object type.
 *
 * @author alexandre.viravout
 */
interface AlbumRepository {
    suspend fun getAlbums(forceRefresh: Boolean = false): Result<List<Album>, Throwable>
    suspend fun getAlbumDetails(id: Int): Result<Album, Throwable>
    fun observeFavoriteIds(): Flow<Set<Int>>
    suspend fun toggleFavorite(id: Int): Result<Boolean, Throwable>
}