package fr.leboncoin.androidrecruitmenttestapp.feature.albums.usecase

import com.github.michaelbull.result.Result
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.domain.model.Album
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.domain.repository.AlbumRepository
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.domain.usecase.GetAlbumDetailsUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
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
    fun `should call repository get albums when use case is invoked`() = runTest {
        // GIVEN
        whenever(repository.getAlbumDetails(id = any())) doReturn any<Result<Album, Throwable>>()

        // WHEN
        target(id = 1)

        // THEN
        verify(repository).getAlbumDetails(id = any())
    }
}