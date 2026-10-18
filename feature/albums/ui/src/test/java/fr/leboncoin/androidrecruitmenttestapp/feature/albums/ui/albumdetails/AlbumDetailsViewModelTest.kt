package fr.leboncoin.androidrecruitmenttestapp.feature.albums.ui.albumdetails

import androidx.lifecycle.SavedStateHandle
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import fr.leboncoin.androidrecruitmenttestapp.core.analytics.AnalyticsHelper
import fr.leboncoin.androidrecruitmenttestapp.core.unittest.MainDispatcherRule
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.domain.model.Album
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.domain.usecase.GetAlbumDetailsUseCase
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.domain.usecase.ObserveFavoriteIdsUseCase
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.domain.usecase.ToggleFavoriteUseCase
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.ui.AlbumConverter
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.ui.AlbumViewState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * This class test AlbumDetailsViewModel class.
 *
 * @author alexandre.viravout
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class AlbumDetailsViewModelTest {

    private companion object {
        const val ALBUM_ID = 42
    }

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val savedStateHandle = SavedStateHandle(initialState = mapOf("albumId" to ALBUM_ID))

    private val getAlbumDetailsUseCase: GetAlbumDetailsUseCase = mock()

    private val observeFavoriteIdsUseCase: ObserveFavoriteIdsUseCase = mock()

    private val toggleFavoriteUseCase: ToggleFavoriteUseCase = mock()

    private val converter: AlbumConverter = mock()

    private val analyticsHelper: AnalyticsHelper = mock()

    private val favoriteIds = MutableStateFlow<Set<Int>>(value = emptySet())

    private fun createViewModel() = AlbumDetailsViewModel(
        savedStateHandle = savedStateHandle,
        getAlbumDetailsUseCase = getAlbumDetailsUseCase,
        observeFavoriteIdsUseCase = observeFavoriteIdsUseCase,
        toggleFavoriteUseCase = toggleFavoriteUseCase,
        converter = converter,
        analyticsHelper = analyticsHelper,
    )

    /**
     * ui state is shared with WhileSubscribed, so it needs a collector to be computed.
     */
    private fun TestScope.collectUiState(viewModel: AlbumDetailsViewModel) {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }
    }

    @Before
    fun setup() = runTest {
        whenever(observeFavoriteIdsUseCase()) doReturn favoriteIds
        whenever(getAlbumDetailsUseCase(id = ALBUM_ID)) doReturn Err(error = Throwable())
    }

    // region load

    @Test
    fun `should initialize ui state to Loading by default`() = runTest {
        // WHEN
        val viewModel = createViewModel()

        // THEN
        Assert.assertEquals(AlbumDetailsUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `should track screen view on init`() = runTest {
        // WHEN
        createViewModel()

        // THEN
        verify(analyticsHelper, times(1)).trackScreenView(screenName = "Details")
        verify(analyticsHelper, times(1)).trackSelection(itemId = ALBUM_ID.toString())
    }

    @Test
    fun `should update ui state with Error when get album details use case return an error`() =
        runTest {
            // GIVEN
            whenever(getAlbumDetailsUseCase(id = ALBUM_ID)) doReturn Err(error = IllegalStateException())

            // WHEN
            val viewModel = createViewModel()
            collectUiState(viewModel = viewModel)
            advanceUntilIdle()

            // THEN
            Assert.assertEquals(
                AlbumDetailsUiState.Error(message = "An error occurred, retry later"),
                viewModel.uiState.value,
            )
        }

    @Test
    fun `should update ui state to Success when get album details use case return an album`() =
        runTest {
            // GIVEN
            givenAlbum()

            // WHEN
            val viewModel = createViewModel()
            collectUiState(viewModel = viewModel)
            advanceUntilIdle()

            // THEN
            Assert.assertEquals(
                AlbumDetailsUiState.Success(album = albumViewState(isFavorite = false)),
                viewModel.uiState.value,
            )
        }

    // endregion

    // region favorite

    @Test
    fun `should mark album as favorite when its id is in favorite ids`() = runTest {
        // GIVEN
        givenAlbum()
        favoriteIds.value = setOf(1, ALBUM_ID)

        // WHEN
        val viewModel = createViewModel()
        collectUiState(viewModel = viewModel)
        advanceUntilIdle()

        // THEN
        Assert.assertEquals(
            AlbumDetailsUiState.Success(album = albumViewState(isFavorite = true)),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `should not mark album as favorite when only other ids are favorites`() = runTest {
        // GIVEN
        givenAlbum()
        favoriteIds.value = setOf(1, 2)

        // WHEN
        val viewModel = createViewModel()
        collectUiState(viewModel = viewModel)
        advanceUntilIdle()

        // THEN
        Assert.assertEquals(
            AlbumDetailsUiState.Success(album = albumViewState(isFavorite = false)),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `should update favorite state when favorite ids change`() = runTest {
        // GIVEN
        givenAlbum()
        val viewModel = createViewModel()
        collectUiState(viewModel = viewModel)
        advanceUntilIdle()

        // WHEN
        favoriteIds.value = setOf(ALBUM_ID)
        advanceUntilIdle()

        // THEN
        Assert.assertEquals(
            AlbumDetailsUiState.Success(album = albumViewState(isFavorite = true)),
            viewModel.uiState.value,
        )

        // WHEN
        favoriteIds.value = emptySet()
        advanceUntilIdle()

        // THEN
        Assert.assertEquals(
            AlbumDetailsUiState.Success(album = albumViewState(isFavorite = false)),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `should keep Error state when favorite ids change`() = runTest {
        // GIVEN
        val viewModel = createViewModel()
        collectUiState(viewModel = viewModel)
        advanceUntilIdle()

        // WHEN
        favoriteIds.value = setOf(ALBUM_ID)
        advanceUntilIdle()

        // THEN
        Assert.assertEquals(
            AlbumDetailsUiState.Error(message = "An error occurred, retry later"),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `should toggle favorite of displayed album when favorite is clicked`() = runTest {
        // GIVEN
        whenever(toggleFavoriteUseCase(id = ALBUM_ID)) doReturn Ok(value = true)
        val viewModel = createViewModel()

        // WHEN
        viewModel.onFavoriteClick()
        advanceUntilIdle()

        // THEN
        verify(toggleFavoriteUseCase, times(1)).invoke(id = ALBUM_ID)
    }

    // endregion

    private suspend fun givenAlbum() {
        val album = mock<Album>()
        whenever(getAlbumDetailsUseCase(id = ALBUM_ID)) doReturn Ok(value = album)
        whenever(converter.convertToViewState(album = album)) doReturn albumViewState()
    }

    private fun albumViewState(isFavorite: Boolean = false) = AlbumViewState(
        id = ALBUM_ID,
        albumId = 1,
        title = "title",
        url = "url",
        thumbnailUrl = "thumbnailUrl",
        isFavorite = isFavorite,
    )
}