package fr.leboncoin.androidrecruitmenttestapp.feature.albums.data.repository

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.data.api.AlbumRemoteDataSource
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.data.api.dto.AlbumDto
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.data.mapper.AlbumMapper
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.domain.model.Album
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * This class test the AlbumDataRepository class.
 *
 * @author alexandre.viravout
 */
@RunWith(MockitoJUnitRunner::class)
class AlbumDataRepositoryTest {

    @Mock
    private lateinit var dataSource: AlbumRemoteDataSource

    private val mapper: AlbumMapper = AlbumMapper()

    private val target: AlbumDataRepository by lazy {
        AlbumDataRepository(
            dataSource = dataSource,
            mapper = mapper,
        )
    }

    // region getAlbums

    @Test
    fun `should get an error when date source throw an exception`() = runTest {
        // GIVEN
        val exception = IllegalStateException()
        whenever(dataSource.getAlbums()) doThrow exception

        // WHEN
        val result = target.getAlbums(forceRefresh = true)

        // THEN
        Assert.assertTrue(result.isErr)
        Assert.assertEquals(exception, result.getError())
    }

    @Test
    fun `should get an album empty list when date source return a dto empty list`() = runTest {
        // GIVEN
        whenever(dataSource.getAlbums()) doReturn emptyList()

        // WHEN
        val result = target.getAlbums(forceRefresh = true)

        // THEN
        Assert.assertTrue(result.isOk)
        Assert.assertEquals(emptyList<Album>(), result.get())
    }

    @Test
    fun `should get albums from api when force refresh is true`() = runTest {
        // GIVEN
        val albumDto1 = albumDto(id = 1)
        val albumDto2 = albumDto(id = 2)
        val albumsDto = listOf(albumDto1, albumDto2)
        whenever(dataSource.getAlbums()) doReturn albumsDto

        // WHEN
        val result = target.getAlbums(forceRefresh = true)

        // THEN
        val albums = albumsDto.map { mapper.mapToDomain(dto = it) }
        Assert.assertTrue(result.isOk)
        Assert.assertEquals(Ok(value = albums), result)
    }

    @Test
    fun `should get albums from cache when cache is not null and force refresh is false`() =
        runTest {
            // GIVEN
            val albumDto1 = albumDto(id = 1)
            val albumDto2 = albumDto(id = 2)
            val albumsDto = listOf(albumDto1, albumDto2)
            whenever(dataSource.getAlbums()) doReturn albumsDto

            // WHEN
            val result = target.getAlbums(forceRefresh = true)
            target.getAlbums(forceRefresh = false)

            // THEN
            verify(dataSource, times(1)).getAlbums()
            val albums = albumsDto.map { mapper.mapToDomain(dto = it) }
            Assert.assertTrue(result.isOk)
            Assert.assertEquals(Ok(value = albums), result)
        }

    // endregion

    // region getAlbumDetails

    @Test
    fun `should get album when album exists`() = runTest {
        // GIVEN
        val dto2 = albumDto(id = 2)
        val dtos = listOf(albumDto(id = 1), dto2, albumDto(id = 3))
        whenever(dataSource.getAlbums()) doReturn dtos

        // WHEN
        val result = target.getAlbumDetails(id = 2)

        // THEN
        val album2 = mapper.mapToDomain(dto2)
        Assert.assertTrue(result.isOk)
        Assert.assertEquals(Ok(value = album2), result)
    }

    @Test
    fun `should return NoSuchElementException when id does not exist`() = runTest {
        // GIVEN
        val dtos = listOf(albumDto(id = 1), albumDto(id = 2))
        whenever(dataSource.getAlbums()) doReturn dtos

        // WHEN
        val result = target.getAlbumDetails(id = 99)

        // THEN
        Assert.assertTrue(result.isErr)
        val error = result.getError()
        Assert.assertTrue(error is NoSuchElementException)
        Assert.assertEquals("Album 99 not found", error?.message)
    }

    @Test
    fun `should return NoSuchElementException when album list is empty`() = runTest {
        // GIVEN
        whenever(dataSource.getAlbums()) doReturn emptyList()

        // WHEN
        val result = target.getAlbumDetails(id = 1)

        // THEN
        Assert.assertTrue(result.isErr)
        Assert.assertTrue(result.getError() is NoSuchElementException)
    }

    // endregion

    private fun albumDto(id: Int) = AlbumDto(
        id = id,
        albumId = 1,
        title = "title $id",
        url = "url $id",
        thumbnailUrl = "thumbnail $id",
    )
}