package fr.leboncoin.androidrecruitmenttestapp.feature.albums.usecase

import com.github.michaelbull.result.Result
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.domain.model.Album
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.domain.repository.AlbumRepository
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.domain.usecase.GetAlbumsUseCase
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
    fun `should call repository get albums when use case is invoked`() = runTest {
        // GIVEN
        whenever(repository.getAlbums(forceRefresh = any())) doReturn any<Result<List<Album>, Throwable>>()

        // WHEN
        target(forceRefresh = true)

        // THEN
        verify(repository).getAlbums(forceRefresh = any())
    }
}