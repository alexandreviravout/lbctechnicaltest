package fr.leboncoin.androidrecruitmenttestapp.albums

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import fr.leboncoin.androidrecruitmenttestapp.domain.model.Album
import fr.leboncoin.androidrecruitmenttestapp.domain.usecase.GetAlbumsUseCase
import fr.leboncoin.androidrecruitmenttestapp.ui.AlbumConverter
import fr.leboncoin.androidrecruitmenttestapp.ui.albums.AlbumsUiState
import fr.leboncoin.androidrecruitmenttestapp.ui.albums.AlbumsViewModel
import fr.leboncoin.androidrecruitmenttestapp.unittest.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
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

    private val converter: AlbumConverter = AlbumConverter()

    private fun createViewModel() = AlbumsViewModel(
        getAlbumsUseCase = getAlbumsUseCase,
        converter = converter,
    )

    private val albums = listOf(album(id = 1), album(id = 2))
    private val errorMessage = "An error occurred, click on the button to retry to load albums"

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
        whenever(getAlbumsUseCase.invoke(forceRefresh = any())) doReturn Err(error = IllegalStateException())
        val viewModel = createViewModel()
        advanceUntilIdle()

        // WHEN
        viewModel.onRetry()

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

    private fun album(id: Int) = Album(
        id = id,
        albumId = 1,
        title = "title $id",
        url = "url $id",
        thumbnailUrl = "thumbnail $id",
    )
}