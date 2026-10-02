package fr.leboncoin.data.repository

import fr.leboncoin.data.network.api.AlbumApiService
import retrofit2.Retrofit
import javax.inject.Inject

class AlbumRepository @Inject constructor(
    private val retrofit: Retrofit,
) {

    private val service by lazy { retrofit.create(AlbumApiService::class.java) }

    suspend fun getAllAlbums() = service.getAlbums()
}