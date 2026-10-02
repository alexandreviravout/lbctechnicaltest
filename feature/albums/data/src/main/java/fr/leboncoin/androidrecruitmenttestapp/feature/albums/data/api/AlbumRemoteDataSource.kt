package fr.leboncoin.androidrecruitmenttestapp.feature.albums.data.api

import fr.leboncoin.androidrecruitmenttestapp.feature.albums.data.api.dto.AlbumDto
import retrofit2.Retrofit
import javax.inject.Inject

/**
 * This class defines remote data source of albums list.
 * It allows us to convert api service data to dto.
 *
 * @author alexandre.viravout
 */
class AlbumRemoteDataSource @Inject constructor(
    private val retrofit: Retrofit,
) {

    private val service by lazy { retrofit.create(AlbumApiService::class.java) }

    suspend fun getAlbums(): List<AlbumDto> =
        service.getAlbums()
}