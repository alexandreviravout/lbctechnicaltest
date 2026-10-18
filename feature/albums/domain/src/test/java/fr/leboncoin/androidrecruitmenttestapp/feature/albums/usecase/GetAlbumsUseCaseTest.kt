package fr.leboncoin.androidrecruitmenttestapp.feature.albums.usecase

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.domain.model.Album
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.domain.repository.AlbumRepository
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.domain.usecase.GetAlbumsUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * This class test GetAlbumsUseCase class.
 *
 * @author alexandre.viravout
 */
@RunWith(MockitoJUnitRunner::class)
class GetAlbumsUseCaseTest {

    @Mock
    private lateinit var repository: AlbumRepository

    private val target by lazy {
        GetAlbumsUseCase(repository = repository)
    }

    @Test
    fun `should call repository get albums with force refresh true when use case is invoked with true`() =
        runTest {
            // GIVEN
            whenever(repository.getAlbums(forceRefresh = true)) doReturn Ok(value = emptyList())

            // WHEN
            target(forceRefresh = true)

            // THEN
            verify(repository, times(1)).getAlbums(forceRefresh = true)
        }

    @Test
    fun `should call repository get albums with force refresh false when use case is invoked with false`() =
        runTest {
            // GIVEN
            whenever(repository.getAlbums(forceRefresh = false)) doReturn Ok(value = emptyList())

            // WHEN
            target(forceRefresh = false)

            // THEN
            verify(repository, times(1)).getAlbums(forceRefresh = false)
        }

    @Test
    fun `should return albums from repository`() = runTest {
        // GIVEN
        val albums = listOf(album(id = 1), album(id = 2))
        whenever(repository.getAlbums(forceRefresh = true)) doReturn Ok(value = albums)

        // WHEN
        val result = target(forceRefresh = true)

        // THEN
        Assert.assertEquals(Ok(value = albums), result)
    }

    @Test
    fun `should return repository error when get albums fails`() = runTest {
        // GIVEN
        val error = IllegalStateException("No network")
        whenever(repository.getAlbums(forceRefresh = true)) doReturn Err(error = error)

        // WHEN
        val result = target(forceRefresh = true)

        // THEN
        Assert.assertEquals(Err(error = error), result)
    }

    private fun album(id: Int) = Album(
        id = id,
        albumId = 1,
        title = "title $id",
        url = "url $id",
        thumbnailUrl = "thumbnail $id",
    )
}