package fr.leboncoin.androidrecruitmenttestapp.feature.albums.data.mapper

import fr.leboncoin.androidrecruitmenttestapp.feature.albums.data.api.dto.AlbumDto
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.data.local.entity.AlbumEntity
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.domain.model.Album
import javax.inject.Inject

/**
 * This mapper map album dto object to domain object.
 *
 * @author alexandre.viravout
 */
class AlbumMapper @Inject constructor() {

    fun mapToDomain(dto: AlbumDto): Album =
        Album(
            id = dto.id,
            albumId = dto.albumId,
            title = dto.title,
            url = dto.url,
            thumbnailUrl = dto.thumbnailUrl,
        )

    fun mapToDomain(entity: AlbumEntity): Album =
        Album(
            id = entity.id,
            albumId = entity.albumId,
            title = entity.title,
            url = entity.url,
            thumbnailUrl = entity.thumbnailUrl,
        )

    fun mapToEntity(dto: AlbumDto): AlbumEntity =
        AlbumEntity(
            id = dto.id,
            albumId = dto.albumId,
            title = dto.title,
            url = dto.url,
            thumbnailUrl = dto.thumbnailUrl,
        )
}