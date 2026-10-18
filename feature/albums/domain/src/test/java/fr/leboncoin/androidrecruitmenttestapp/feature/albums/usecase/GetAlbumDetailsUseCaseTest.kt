package fr.leboncoin.androidrecruitmenttestapp.feature.albums.usecase

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.domain.model.Album
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.domain.repository.AlbumRepository
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.domain.usecase.GetAlbumDetailsUseCase
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
 * This class test GetAlbumDetailsUseCase class.
 *
 * @author alexandre.viravout
 */
@RunWith(MockitoJUnitRunner::class)
class GetAlbumDetailsUseCaseTest {

    @Mock
    private lateinit var repository: AlbumRepository

    private val target by lazy {
        GetAlbumDetailsUseCase(repository = repository)
    }

    @Test
    fun `should call repository get album details with album id when use case is invoked`() =
        runTest {
            // GIVEN
            whenever(repository.getAlbumDetails(id = 42)) doReturn Ok(value = album(id = 42))

            // WHEN
            target(id = 42)

            // THEN
            verify(repository, times(1)).getAlbumDetails(id = 42)
        }

    @Test
    fun `should return album from repository`() = runTest {
        // GIVEN
        val album = album(id = 42)
        whenever(repository.getAlbumDetails(id = 42)) doReturn Ok(value = album)

        // WHEN
        val result = target(id = 42)

        // THEN
        Assert.assertEquals(Ok(value = album), result)
    }

    @Test
    fun `should return repository error when album does not exist`() = runTest {
        // GIVEN
        val error = NoSuchElementException("Album 99 not found")
        whenever(repository.getAlbumDetails(id = 99)) doReturn Err(error = error)

        // WHEN
        val result = target(id = 99)

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