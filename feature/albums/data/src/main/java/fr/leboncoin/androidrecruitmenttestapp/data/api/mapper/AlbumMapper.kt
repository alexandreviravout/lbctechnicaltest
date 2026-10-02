package fr.leboncoin.androidrecruitmenttestapp.data.api.mapper

import fr.leboncoin.androidrecruitmenttestapp.data.api.dto.AlbumDto
import fr.leboncoin.androidrecruitmenttestapp.domain.model.Album
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
}