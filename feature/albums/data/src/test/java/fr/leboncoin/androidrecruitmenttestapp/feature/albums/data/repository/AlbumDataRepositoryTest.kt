package fr.leboncoin.androidrecruitmenttestapp.feature.albums.data.repository

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.getError
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.data.api.AlbumRemoteDataSource
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.data.api.dto.AlbumDto
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.data.local.AlbumLocalDataSource
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.data.local.entity.AlbumEntity
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.data.mapper.AlbumMapper
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever

/**
 * This class test the AlbumDataRepository class.
 *
 * @author alexandre.viravout
 */
@RunWith(MockitoJUnitRunner::class)
class AlbumDataRepositoryTest {

    @Mock
    private lateinit var remoteDataSource: AlbumRemoteDataSource

    @Mock
    private lateinit var localDataSource: AlbumLocalDataSource

    private val mapper: AlbumMapper = AlbumMapper()

    private val target: AlbumDataRepository by lazy {
        AlbumDataRepository(
            remoteDataSource = remoteDataSource,
            localDataSource = localDataSource,
            mapper = mapper,
        )
    }

    // region getAlbums - force refresh

    @Test
    fun `should return remote albums when force refresh is true and remote succeeds`() = runTest {
        // GIVEN
        val dtos = listOf(albumDto(id = 1), albumDto(id = 2))
        whenever(remoteDataSource.getAlbums()) doReturn dtos

        // WHEN
        val result = target.getAlbums(forceRefresh = true)

        // THEN
        Assert.assertEquals(Ok(value = dtos.map { mapper.mapToDomain(dto = it) }), result)
    }

    @Test
    fun `should save remote albums in database when force refresh is true and remote succeeds`() =
        runTest {
            // GIVEN
            val dtos = listOf(albumDto(id = 1), albumDto(id = 2))
            whenever(remoteDataSource.getAlbums()) doReturn dtos

            // WHEN
            target.getAlbums(forceRefresh = true)

            // THEN
            verify(localDataSource, times(1))
                .saveAlbums(albums = dtos.map { mapper.mapToEntity(dto = it) })
        }

    @Test
    fun `should not read database when force refresh is true and remote succeeds`() = runTest {
        // GIVEN
        whenever(remoteDataSource.getAlbums()) doReturn listOf(albumDto(id = 1))

        // WHEN
        target.getAlbums(forceRefresh = true)

        // THEN
        verify(localDataSource, never()).getAlbums()
    }

    @Test
    fun `should return stored albums when remote returns an empty list and database is not empty`() =
        runTest {
            // GIVEN
            val entities = listOf(albumEntity(id = 1), albumEntity(id = 2))
            whenever(remoteDataSource.getAlbums()) doReturn emptyList()
            whenever(localDataSource.getAlbums()) doReturn entities

            // WHEN
            val result = target.getAlbums(forceRefresh = true)

            // THEN
            Assert.assertEquals(
                Ok(value = entities.map { mapper.mapToDomain(entity = it) }),
                result,
            )
        }

    @Test
    fun `should not overwrite database when remote returns an empty list`() = runTest {
        // GIVEN
        whenever(remoteDataSource.getAlbums()) doReturn emptyList()
        whenever(localDataSource.getAlbums()) doReturn listOf(albumEntity(id = 1))

        // WHEN
        target.getAlbums(forceRefresh = true)

        // THEN
        verify(localDataSource, never()).saveAlbums(albums = any())
    }

    @Test
    fun `should return NoSuchElementException when remote returns an empty list and database is empty`() =
        runTest {
            // GIVEN
            whenever(remoteDataSource.getAlbums()) doReturn emptyList()
            whenever(localDataSource.getAlbums()) doReturn emptyList()

            // WHEN
            val result = target.getAlbums(forceRefresh = true)

            // THEN
            Assert.assertTrue(result.isErr)
            val error = result.getError()
            Assert.assertTrue(error is NoSuchElementException)
            Assert.assertEquals("No albums available", error?.message)
        }

    @Test
    fun `should return stored albums when remote fails and database is not empty`() = runTest {
        // GIVEN
        val entities = listOf(albumEntity(id = 1), albumEntity(id = 2))
        whenever(remoteDataSource.getAlbums()) doThrow IllegalStateException("No network")
        whenever(localDataSource.getAlbums()) doReturn entities

        // WHEN
        val result = target.getAlbums(forceRefresh = true)

        // THEN
        Assert.assertEquals(Ok(value = entities.map { mapper.mapToDomain(entity = it) }), result)
    }

    @Test
    fun `should return remote error when remote fails and database is empty`() = runTest {
        // GIVEN
        val remoteError = IllegalStateException("No network")
        whenever(remoteDataSource.getAlbums()) doThrow remoteError
        whenever(localDataSource.getAlbums()) doReturn emptyList()

        // WHEN
        val result = target.getAlbums(forceRefresh = true)

        // THEN
        Assert.assertTrue(result.isErr)
        Assert.assertEquals(remoteError, result.getError())
    }

    @Test
    fun `should return remote error when remote fails and database read fails`() = runTest {
        // GIVEN
        val remoteError = IllegalStateException("No network")
        whenever(remoteDataSource.getAlbums()) doThrow remoteError
        whenever(localDataSource.getAlbums()) doThrow IllegalStateException("Database error")

        // WHEN
        val result = target.getAlbums(forceRefresh = true)

        // THEN
        Assert.assertTrue(result.isErr)
        Assert.assertEquals(remoteError, result.getError())
    }

    @Test
    fun `should return stored albums when saving remote albums in database fails`() = runTest {
        // GIVEN
        val dtos = listOf(albumDto(id = 3))
        val storedEntities = listOf(albumEntity(id = 1), albumEntity(id = 2))
        whenever(remoteDataSource.getAlbums()) doReturn dtos
        whenever(localDataSource.saveAlbums(albums = dtos.map { mapper.mapToEntity(dto = it) }))
            .doThrow(IllegalStateException("Database error"))
        whenever(localDataSource.getAlbums()) doReturn storedEntities

        // WHEN
        val result = target.getAlbums(forceRefresh = true)

        // THEN
        Assert.assertEquals(
            Ok(value = storedEntities.map { mapper.mapToDomain(entity = it) }),
            result,
        )
    }

    @Test
    fun `should return save error when saving fails and database is empty`() = runTest {
        // GIVEN
        val dtos = listOf(albumDto(id = 1))
        val saveError = IllegalStateException("Database error")
        whenever(remoteDataSource.getAlbums()) doReturn dtos
        whenever(localDataSource.saveAlbums(albums = dtos.map { mapper.mapToEntity(dto = it) }))
            .doThrow(saveError)
        whenever(localDataSource.getAlbums()) doReturn emptyList()

        // WHEN
        val result = target.getAlbums(forceRefresh = true)

        // THEN
        Assert.assertTrue(result.isErr)
        Assert.assertEquals(saveError, result.getError())
    }

    @Test
    fun `should rethrow cancellation without falling back on database`() = runTest {
        // GIVEN
        val cancellation = CancellationException("Cancelled")
        whenever(remoteDataSource.getAlbums()) doThrow cancellation

        // WHEN
        val thrown = try {
            target.getAlbums(forceRefresh = true)
            null
        } catch (e: CancellationException) {
            e
        }

        // THEN
        Assert.assertEquals(cancellation, thrown)
        verify(localDataSource, never()).getAlbums()
    }

    // endregion

    // region getAlbums - no force refresh

    @Test
    fun `should return stored albums without calling remote when database is not empty`() =
        runTest {
            // GIVEN
            val entities = listOf(albumEntity(id = 1), albumEntity(id = 2))
            whenever(localDataSource.getAlbums()) doReturn entities

            // WHEN
            val result = target.getAlbums(forceRefresh = false)

            // THEN
            verifyNoInteractions(remoteDataSource)
            Assert.assertEquals(
                Ok(value = entities.map { mapper.mapToDomain(entity = it) }),
                result,
            )
        }

    @Test
    fun `should not force refresh by default`() = runTest {
        // GIVEN
        whenever(localDataSource.getAlbums()) doReturn listOf(albumEntity(id = 1))

        // WHEN
        target.getAlbums()

        // THEN
        verifyNoInteractions(remoteDataSource)
    }

    @Test
    fun `should return remote albums when database is empty`() = runTest {
        // GIVEN
        val dtos = listOf(albumDto(id = 1), albumDto(id = 2))
        whenever(localDataSource.getAlbums()) doReturn emptyList()
        whenever(remoteDataSource.getAlbums()) doReturn dtos

        // WHEN
        val result = target.getAlbums(forceRefresh = false)

        // THEN
        verify(remoteDataSource, times(1)).getAlbums()
        verify(localDataSource, times(1))
            .saveAlbums(albums = dtos.map { mapper.mapToEntity(dto = it) })
        Assert.assertEquals(Ok(value = dtos.map { mapper.mapToDomain(dto = it) }), result)
    }

    @Test
    fun `should return remote albums when database read fails`() = runTest {
        // GIVEN
        val dtos = listOf(albumDto(id = 1))
        whenever(localDataSource.getAlbums()) doThrow IllegalStateException("Database error")
        whenever(remoteDataSource.getAlbums()) doReturn dtos

        // WHEN
        val result = target.getAlbums(forceRefresh = false)

        // THEN
        Assert.assertEquals(Ok(value = dtos.map { mapper.mapToDomain(dto = it) }), result)
    }

    @Test
    fun `should return remote error when database is empty and remote fails`() = runTest {
        // GIVEN
        val remoteError = IllegalStateException("No network")
        whenever(localDataSource.getAlbums()) doReturn emptyList()
        whenever(remoteDataSource.getAlbums()) doThrow remoteError

        // WHEN
        val result = target.getAlbums(forceRefresh = false)

        // THEN
        verify(localDataSource, times(2)).getAlbums()
        Assert.assertTrue(result.isErr)
        Assert.assertEquals(remoteError, result.getError())
    }

    @Test
    fun `should return NoSuchElementException when database is empty and remote returns an empty list`() =
        runTest {
            // GIVEN
            whenever(localDataSource.getAlbums()) doReturn emptyList()
            whenever(remoteDataSource.getAlbums()) doReturn emptyList()

            // WHEN
            val result = target.getAlbums(forceRefresh = false)

            // THEN
            verify(localDataSource, never()).saveAlbums(albums = any())
            Assert.assertTrue(result.isErr)
            Assert.assertTrue(result.getError() is NoSuchElementException)
        }

    // endregion

    // region getAlbumDetails

    @Test
    fun `should return album from database without calling remote when album exists`() =
        runTest {
            // GIVEN
            val entity = albumEntity(id = 2)
            whenever(localDataSource.getAlbum(id = 2)) doReturn entity

            // WHEN
            val result = target.getAlbumDetails(id = 2)

            // THEN
            verifyNoInteractions(remoteDataSource)
            Assert.assertEquals(Ok(value = mapper.mapToDomain(entity = entity)), result)
        }

    @Test
    fun `should return NoSuchElementException when album does not exist`() = runTest {
        // GIVEN
        whenever(localDataSource.getAlbum(id = 99)) doReturn null

        // WHEN
        val result = target.getAlbumDetails(id = 99)

        // THEN
        Assert.assertTrue(result.isErr)
        val error = result.getError()
        Assert.assertTrue(error is NoSuchElementException)
        Assert.assertEquals("Album 99 not found", error?.message)
    }

    @Test
    fun `should return database error when database read fails`() = runTest {
        // GIVEN
        val databaseError = IllegalStateException("Database error")
        whenever(localDataSource.getAlbum(id = 1)) doThrow databaseError

        // WHEN
        val result = target.getAlbumDetails(id = 1)

        // THEN
        Assert.assertTrue(result.isErr)
        Assert.assertEquals(databaseError, result.getError())
    }

    // endregion

    // region favorites

    @Test
    fun `should observe favorite ids as a set`() = runTest {
        // GIVEN
        whenever(localDataSource.observeFavoriteIds()) doReturn flowOf(listOf(1, 3, 3))

        // WHEN
        val result = target.observeFavoriteIds().first()

        // THEN
        Assert.assertEquals(setOf(1, 3), result)
    }

    @Test
    fun `should return new favorite state when toggle succeeds`() = runTest {
        // GIVEN
        whenever(localDataSource.toggleFavorite(id = 1)) doReturn true

        // WHEN
        val result = target.toggleFavorite(id = 1)

        // THEN
        Assert.assertEquals(Ok(value = true), result)
    }

    @Test
    fun `should return database error when toggle fails`() = runTest {
        // GIVEN
        val databaseError = IllegalStateException("Database error")
        whenever(localDataSource.toggleFavorite(id = 1)) doThrow databaseError

        // WHEN
        val result = target.toggleFavorite(id = 1)

        // THEN
        Assert.assertTrue(result.isErr)
        Assert.assertEquals(databaseError, result.getError())
    }

    // endregion

    private fun albumDto(id: Int) = AlbumDto(
        id = id,
        albumId = 1,
        title = "title $id",
        url = "url $id",
        thumbnailUrl = "thumbnail $id",
    )

    private fun albumEntity(id: Int) = AlbumEntity(
        id = id,
        albumId = 1,
        title = "title $id",
        url = "url $id",
        thumbnailUrl = "thumbnail $id",
    )
}