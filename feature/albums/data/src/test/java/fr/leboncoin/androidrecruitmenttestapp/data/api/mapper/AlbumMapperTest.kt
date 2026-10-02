package fr.leboncoin.androidrecruitmenttestapp.data.api.mapper

import fr.leboncoin.androidrecruitmenttestapp.data.api.dto.AlbumDto
import org.junit.Assert
import org.junit.Test

/**
 * This class test album mapper class.
 *
 * @author alexandre.viravout
 */
class AlbumMapperTest {

    private val target: AlbumMapper = AlbumMapper()

    @Test
    fun `should map album dto to domain object`() {
        // GIVEN
        val id = 1
        val albumId = 1
        val title = "title"
        val url = "url"
        val thumbnailUrl = "thumbnailUrl"
        val dto = AlbumDto(id, albumId, title, url, thumbnailUrl)

        // WHEN
        val result = target.mapToDomain(dto)

        // THEN
        Assert.assertEquals(id, result.id)
        Assert.assertEquals(albumId, result.albumId)
        Assert.assertEquals(title, result.title)
        Assert.assertEquals(url, result.url)
        Assert.assertEquals(thumbnailUrl, result.thumbnailUrl)
    }
}