package fr.leboncoin.androidrecruitmenttestapp.feature.albums.ui

import fr.leboncoin.androidrecruitmenttestapp.feature.albums.domain.model.Album
import org.junit.Assert
import org.junit.Test

/**
 * This class test AlbumConverter class.
 *
 * @author alexandre.viravout
 */
class AlbumConverterTest {

    private val target: AlbumConverter = AlbumConverter()

    // region convertToViewState(albums)

    @Test
    fun `should map every album and keep order`() {
        // GIVEN
        val albums = listOf(album(id = 3), album(id = 1), album(id = 2))

        // WHEN
        val result = target.convertToViewState(albums = albums)

        // THEN
        val expected =
            listOf(albumViewState(id = 3), albumViewState(id = 1), albumViewState(id = 2))
        Assert.assertEquals(expected, result)
    }

    @Test
    fun `should return empty list when albums are empty`() {
        // WHEN
        val result = target.convertToViewState(albums = emptyList())

        // THEN
        Assert.assertEquals(emptyList<AlbumViewState>(), result)
    }

    // endregion

    // region convertToViewState(album)

    @Test
    fun `should convert album to view state`() {
        // GIVEN
        val album = Album(
            id = 1,
            albumId = 2,
            title = "title",
            url = "url",
            thumbnailUrl = "thumbnailUrl",
        )

        // WHEN
        val result = target.convertToViewState(album = album)

        // THEN
        val expected = AlbumViewState(
            id = 1,
            albumId = 2,
            title = "title",
            url = "url",
            thumbnailUrl = "thumbnailUrl",
        )
        Assert.assertEquals(expected, result)
    }

    // endregion

    private fun album(id: Int) = Album(
        id = id,
        albumId = id + 100,
        title = "title $id",
        url = "url $id",
        thumbnailUrl = "thumbnail $id",
    )

    private fun albumViewState(id: Int) = AlbumViewState(
        id = id,
        albumId = id + 100,
        title = "title $id",
        url = "url $id",
        thumbnailUrl = "thumbnail $id",
    )
}