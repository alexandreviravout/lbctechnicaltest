package fr.leboncoin.androidrecruitmenttestapp.data.repository

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.onOk
import fr.leboncoin.androidrecruitmenttestapp.data.api.AlbumRemoteDataSource
import fr.leboncoin.androidrecruitmenttestapp.data.api.mapper.AlbumMapper
import fr.leboncoin.androidrecruitmenttestapp.domain.model.Album
import fr.leboncoin.androidrecruitmenttestapp.domain.repository.AlbumRepository
import javax.inject.Inject

/**
 * This class defines album data repository.
 * Dto objects are mapped to result of domain objects or error.
 * There is a cache to avoid to call remote api service too often.
 *
 * @author alexandre.viravout
 */
class AlbumDataRepository @Inject constructor(
    private val dataSource: AlbumRemoteDataSource,
    private val mapper: AlbumMapper,
) : AlbumRepository {

    private var cachedAlbums: List<Album>? = null

    override suspend fun getAlbums(forceRefresh: Boolean): Result<List<Album>, Throwable> {
        val cached = cachedAlbums
        if (cached != null && !forceRefresh) {
            return Ok(value = cached)
        }

        return runSuspendCatching {
            dataSource.getAlbums().map {
                mapper.mapToDomain(dto = it)
            }
        }.onOk { cachedAlbums = it }
    }

    override suspend fun getAlbumDetails(id: Int): Result<Album, Throwable> =
        getAlbums()
            .andThen { albums ->
                albums.firstOrNull { it.id == id }
                    ?.let { Ok(value = it) }
                    ?: Err(error = NoSuchElementException("Album $id not found"))
            }
}