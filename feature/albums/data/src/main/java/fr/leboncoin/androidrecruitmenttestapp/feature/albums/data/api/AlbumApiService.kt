package fr.leboncoin.androidrecruitmenttestapp.feature.albums.data.api

import fr.leboncoin.androidrecruitmenttestapp.feature.albums.data.api.dto.AlbumDto
import retrofit2.http.GET

/**
 * This interface defines album api service.
 * It allows us to get albums from remote api service.
 *
 * @author alexandre.viravout
 */
interface AlbumApiService {

    @GET("img/shared/technical-test.json")
    suspend fun getAlbums(): List<AlbumDto>
}