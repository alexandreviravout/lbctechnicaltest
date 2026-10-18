package fr.leboncoin.androidrecruitmenttestapp.feature.albums.ui.albums

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import fr.leboncoin.androidrecruitmenttestapp.core.unittest.MainDispatcherRule
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.domain.model.Album
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.domain.usecase.GetAlbumsUseCase
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.domain.usecase.ObserveFavoriteIdsUseCase
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.domain.usecase.ToggleFavoriteUseCase
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.ui.AlbumConverter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doSuspendableAnswer
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * This class test AlbumsViewModel class.
 *
 * @author alexandre.viravout
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class AlbumsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var getAlbumsUseCase: GetAlbumsUseCase

    @Mock
    private lateinit var toggleFavoriteUseCase: ToggleFavoriteUseCase

    @Mock
    private lateinit var observeFavoriteIdsUseCase: ObserveFavoriteIdsUseCase

    private val converter: AlbumConverter = AlbumConverter()

    private val favoriteIds = MutableStateFlow<Set<Int>>(value = emptySet())

    private fun createViewModel() = AlbumsViewModel(
        getAlbumsUseCase = getAlbumsUseCase,
        toggleFavoriteUseCase = toggleFavoriteUseCase,
        observeFavoriteIdsUseCase = observeFavoriteIdsUseCase,
        converter = converter,
    )

    /**
     * ui state is shared with WhileSubscribed, so it needs a collector to be computed.
     */
    private fun TestScope.collectUiState(viewModel: AlbumsViewModel) {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }
    }

    private val albums = listOf(album(id = 1), album(id = 2))
    private val errorMessage = "An error occurred, click on the button to retry to load albums"

    @Before
    fun setup() {
        whenever(observeFavoriteIdsUseCase()) doReturn favoriteIds
    }

    // region init

    @Test
    fun `should initialize ui state to Loading by default`() = runTest {
        // GIVEN
        whenever(getAlbumsUseCase.invoke(forceRefresh = any())) doReturn Ok(value = albums)

        // WHEN
        val viewModel = createViewModel()

        // THEN
        Assert.assertEquals(AlbumsUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `should update ui state to Success with album view state list when get albums use case return albums`() =
        runTest {
            // GIVEN
            whenever(getAlbumsUseCase.invoke(forceRefresh = any())) doReturn Ok(value = albums)

            // WHEN
            val viewModel = createViewModel()
            collectUiState(viewModel = viewModel)
            advanceUntilIdle()

            // THEN
            val albumViewStates = converter.convertToViewState(albums)
            val expected = AlbumsUiState.Success(albums = albumViewStates)
            Assert.assertEquals(expected, viewModel.uiState.value)
        }

    @Test
    fun `should update ui state to Error when get albums use case fails`() = runTest {
        // GIVEN
        whenever(getAlbumsUseCase.invoke(forceRefresh = any())) doReturn Err(error = IllegalStateException())

        // WHEN
        val viewModel = createViewModel()
        collectUiState(viewModel = viewModel)
        advanceUntilIdle()

        // THEN
        Assert.assertEquals(AlbumsUiState.Error(message = errorMessage), viewModel.uiState.value)
    }

    @Test
    fun `should load albums with forceRefresh true on init`() = runTest {
        // GIVEN
        whenever(getAlbumsUseCase.invoke(forceRefresh = any())) doReturn Ok(value = albums)

        // WHEN
        createViewModel()
        advanceUntilIdle()

        // THEN
        verify(getAlbumsUseCase, times(1)).invoke(forceRefresh = true)
    }

    // endregion

    // region onRetry

    @Test
    fun `should update ui state to Loading when on retry is called`() = runTest {
        // GIVEN
        whenever(getAlbumsUseCase.invoke(forceRefresh = any()))
            .doReturn(Err(error = IllegalStateException()))
            .doSuspendableAnswer {
                delay(timeMillis = 1_000)
                Ok(value = albums)
            }
        val viewModel = createViewModel()
        collectUiState(viewModel = viewModel)
        advanceUntilIdle()

        // WHEN
        viewModel.onRetry()
        runCurrent()

        // THEN
        Assert.assertEquals(AlbumsUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `should show albums when retry succeeds when retry is called`() = runTest {
        // GIVEN
        whenever(getAlbumsUseCase.invoke(forceRefresh = any()))
            .doReturn(Err(error = RuntimeException("network down")))
            .doReturn(Ok(value = albums))
        val viewModel = createViewModel()
        collectUiState(viewModel = viewModel)
        advanceUntilIdle()

        // WHEN
        viewModel.onRetry()
        advanceUntilIdle()

        // THEN
        val expected = AlbumsUiState.Success(albums = converter.convertToViewState(albums))
        Assert.assertEquals(expected, viewModel.uiState.value)
    }

    @Test
    fun `should show error again when retry fails when retry is called`() = runTest {
        // GIVEN
        whenever(getAlbumsUseCase.invoke(forceRefresh = any())) doReturn Err(error = IllegalStateException())
        val viewModel = createViewModel()
        collectUiState(viewModel = viewModel)
        advanceUntilIdle()

        // WHEN
        viewModel.onRetry()
        advanceUntilIdle()

        // THEN
        Assert.assertEquals(AlbumsUiState.Error(message = errorMessage), viewModel.uiState.value)
    }

    @Test
    fun `should call use case again when retry is called`() = runTest {
        // GIVEN
        whenever(getAlbumsUseCase.invoke(forceRefresh = any())) doReturn Err(error = IllegalStateException())
        val viewModel = createViewModel()
        advanceUntilIdle()

        // WHEN
        viewModel.onRetry()
        advanceUntilIdle()

        // THEN
        verify(getAlbumsUseCase, times(2)).invoke(forceRefresh = true)
    }

    // endregion

    // region favorites

    @Test
    fun `should mark only favorite albums as favorite`() = runTest {
        // GIVEN
        whenever(getAlbumsUseCase.invoke(forceRefresh = any())) doReturn Ok(value = albums)
        favoriteIds.value = setOf(2)

        // WHEN
        val viewModel = createViewModel()
        collectUiState(viewModel = viewModel)
        advanceUntilIdle()

        // THEN
        val state = viewModel.uiState.value as AlbumsUiState.Success
        Assert.assertEquals(false, state.albums.first { it.id == 1 }.isFavorite)
        Assert.assertEquals(true, state.albums.first { it.id == 2 }.isFavorite)
    }

    @Test
    fun `should update albums when favorite ids change`() = runTest {
        // GIVEN
        whenever(getAlbumsUseCase.invoke(forceRefresh = any())) doReturn Ok(value = albums)
        val viewModel = createViewModel()
        collectUiState(viewModel = viewModel)
        advanceUntilIdle()

        // WHEN
        favoriteIds.value = setOf(1)
        advanceUntilIdle()

        // THEN
        val state = viewModel.uiState.value as AlbumsUiState.Success
        Assert.assertEquals(true, state.albums.first { it.id == 1 }.isFavorite)
        Assert.assertEquals(false, state.albums.first { it.id == 2 }.isFavorite)
    }

    @Test
    fun `should not reload albums when favorite ids change`() = runTest {
        // GIVEN
        whenever(getAlbumsUseCase.invoke(forceRefresh = any())) doReturn Ok(value = albums)
        val viewModel = createViewModel()
        collectUiState(viewModel = viewModel)
        advanceUntilIdle()

        // WHEN
        favoriteIds.value = setOf(1)
        advanceUntilIdle()

        // THEN
        verify(getAlbumsUseCase, times(1)).invoke(forceRefresh = true)
    }

    @Test
    fun `should keep Error state when favorite ids change`() = runTest {
        // GIVEN
        whenever(getAlbumsUseCase.invoke(forceRefresh = any())) doReturn Err(error = IllegalStateException())
        val viewModel = createViewModel()
        collectUiState(viewModel = viewModel)
        advanceUntilIdle()

        // WHEN
        favoriteIds.value = setOf(1)
        advanceUntilIdle()

        // THEN
        Assert.assertEquals(AlbumsUiState.Error(message = errorMessage), viewModel.uiState.value)
    }

    @Test
    fun `should toggle favorite of clicked album when favorite is clicked`() = runTest {
        // GIVEN
        whenever(getAlbumsUseCase.invoke(forceRefresh = any())) doReturn Ok(value = albums)
        whenever(toggleFavoriteUseCase(id = 2)) doReturn Ok(value = true)
        val viewModel = createViewModel()
        advanceUntilIdle()

        // WHEN
        viewModel.onFavoriteClick(id = 2)
        advanceUntilIdle()

        // THEN
        verify(toggleFavoriteUseCase, times(1)).invoke(id = 2)
    }

    // endregion

    private fun album(id: Int) = Album(
        id = id,
        albumId = 1,
        title = "title $id",
        url = "url $id",
        thumbnailUrl = "thumbnail $id",
    )
}