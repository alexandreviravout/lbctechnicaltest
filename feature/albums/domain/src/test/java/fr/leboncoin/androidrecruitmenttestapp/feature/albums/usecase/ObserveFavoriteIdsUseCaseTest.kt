package fr.leboncoin.androidrecruitmenttestapp.feature.albums.usecase

import fr.leboncoin.androidrecruitmenttestapp.feature.albums.domain.repository.AlbumRepository
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.domain.usecase.ObserveFavoriteIdsUseCase
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
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
 * This class test ObserveFavoriteIdsUseCase class.
 *
 * @author alexandre.viravout
 */
@RunWith(MockitoJUnitRunner::class)
class ObserveFavoriteIdsUseCaseTest {

    @Mock
    private lateinit var repository: AlbumRepository

    private val target by lazy {
        ObserveFavoriteIdsUseCase(repository = repository)
    }

    @Test
    fun `should call repository observe favorite ids when use case is invoked`() {
        // GIVEN
        whenever(repository.observeFavoriteIds()) doReturn flowOf(emptySet())

        // WHEN
        target()

        // THEN
        verify(repository, times(1)).observeFavoriteIds()
    }

    @Test
    fun `should emit favorite ids from repository`() = runTest {
        // GIVEN
        val favoriteIds = setOf(1, 3)
        whenever(repository.observeFavoriteIds()) doReturn flowOf(favoriteIds)

        // WHEN
        val result = target().toList()

        // THEN
        Assert.assertEquals(listOf(favoriteIds), result)
    }

    @Test
    fun `should emit every favorite ids update from repository`() = runTest {
        // GIVEN
        val updates = listOf(emptySet(), setOf(1), setOf(1, 2), setOf(2))
        whenever(repository.observeFavoriteIds()) doReturn flowOf(*updates.toTypedArray())

        // WHEN
        val result = target().toList()

        // THEN
        Assert.assertEquals(updates, result)
    }
}