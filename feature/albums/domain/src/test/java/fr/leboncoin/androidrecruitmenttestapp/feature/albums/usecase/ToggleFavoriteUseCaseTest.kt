package fr.leboncoin.androidrecruitmenttestapp.feature.albums.usecase

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.domain.repository.AlbumRepository
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.domain.usecase.ToggleFavoriteUseCase
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
 * This class test ToggleFavoriteUseCase class.
 *
 * @author alexandre.viravout
 */
@RunWith(MockitoJUnitRunner::class)
class ToggleFavoriteUseCaseTest {

    @Mock
    private lateinit var repository: AlbumRepository

    private val target by lazy {
        ToggleFavoriteUseCase(repository = repository)
    }

    @Test
    fun `should call repository toggle favorite with album id when use case is invoked`() =
        runTest {
            // GIVEN
            whenever(repository.toggleFavorite(id = 42)) doReturn Ok(value = true)

            // WHEN
            target(id = 42)

            // THEN
            verify(repository, times(1)).toggleFavorite(id = 42)
        }

    @Test
    fun `should return true when album is added to favorites`() = runTest {
        // GIVEN
        whenever(repository.toggleFavorite(id = 1)) doReturn Ok(value = true)

        // WHEN
        val result = target(id = 1)

        // THEN
        Assert.assertEquals(Ok(value = true), result)
    }

    @Test
    fun `should return false when album is removed from favorites`() = runTest {
        // GIVEN
        whenever(repository.toggleFavorite(id = 1)) doReturn Ok(value = false)

        // WHEN
        val result = target(id = 1)

        // THEN
        Assert.assertEquals(Ok(value = false), result)
    }

    @Test
    fun `should return repository error when toggle fails`() = runTest {
        // GIVEN
        val error = IllegalStateException("Database error")
        whenever(repository.toggleFavorite(id = 1)) doReturn Err(error = error)

        // WHEN
        val result = target(id = 1)

        // THEN
        Assert.assertEquals(Err(error = error), result)
    }
}