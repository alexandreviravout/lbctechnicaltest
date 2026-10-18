package fr.leboncoin.androidrecruitmenttestapp.feature.albums.data.repository

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.get
import com.github.michaelbull.result.orElse
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.data.api.AlbumRemoteDataSource
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.data.local.AlbumLocalDataSource
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.data.mapper.AlbumMapper
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.domain.model.Album
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.domain.repository.AlbumRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * This class defines album data repository.
 *
 * Dto objects are mapped to result of domain objects or error.
 * There is a cache to avoid to call remote api service too often.
 *
 * @author alexandre.viravout
 */
@Singleton
class AlbumDataRepository @Inject constructor(
    private val remoteDataSource: AlbumRemoteDataSource,
    private val localDataSource: AlbumLocalDataSource,
    private val mapper: AlbumMapper,
) : AlbumRepository {

    override suspend fun getAlbums(forceRefresh: Boolean): Result<List<Album>, Throwable> {
        if (!forceRefresh) {
            val localResult = getCachedAlbums()
            val cached = localResult.get()
            if (!cached.isNullOrEmpty()) return localResult
        }

        return refreshAlbums().orElse { remoteError ->
            // Offline mode: fall back on stored albums if we have any
            val localResult = getCachedAlbums()
            val cached = localResult.get()
            if (!cached.isNullOrEmpty()) localResult else Err(error = remoteError)
        }
    }

    override suspend fun getAlbumDetails(id: Int): Result<Album, Throwable> =
        runSuspendCatching {
            localDataSource.getAlbum(id = id)
        }.andThen { entity ->
            entity?.let {
                Ok(value = mapper.mapToDomain(entity = it))
            } ?: Err(error = NoSuchElementException("Album $id not found"))
        }

    override fun observeFavoriteIds(): Flow<Set<Int>> =
        localDataSource.observeFavoriteIds().map { it.toSet() }

    override suspend fun toggleFavorite(id: Int): Result<Boolean, Throwable> =
        runSuspendCatching { localDataSource.toggleFavorite(id = id) }

    private suspend fun refreshAlbums(): Result<List<Album>, Throwable> = runSuspendCatching {
        val dtos = remoteDataSource.getAlbums()
        // An empty remote list must not wipe the stored albums: treat it as a failure
        if (dtos.isEmpty()) throw NoSuchElementException("No albums available")

        val entities = dtos.map {
            mapper.mapToEntity(dto = it)
        }
        localDataSource.saveAlbums(albums = entities)

        entities.map {
            mapper.mapToDomain(entity = it)
        }
    }

    private suspend fun getCachedAlbums(): Result<List<Album>, Throwable> = runSuspendCatching {
        localDataSource.getAlbums().map {
            mapper.mapToDomain(entity = it)
        }
    }
}