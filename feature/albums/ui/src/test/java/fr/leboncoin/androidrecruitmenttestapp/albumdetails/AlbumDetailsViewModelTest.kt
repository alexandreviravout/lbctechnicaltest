package fr.leboncoin.androidrecruitmenttestapp.albumdetails

import androidx.lifecycle.SavedStateHandle
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import fr.leboncoin.androidrecruitmenttestapp.analytics.AnalyticsHelper
import fr.leboncoin.androidrecruitmenttestapp.domain.model.Album
import fr.leboncoin.androidrecruitmenttestapp.domain.usecase.GetAlbumDetailsUseCase
import fr.leboncoin.androidrecruitmenttestapp.ui.AlbumConverter
import fr.leboncoin.androidrecruitmenttestapp.ui.AlbumViewState
import fr.leboncoin.androidrecruitmenttestapp.ui.albumdetails.AlbumDetailsUiState
import fr.leboncoin.androidrecruitmenttestapp.ui.albumdetails.AlbumDetailsViewModel
import fr.leboncoin.androidrecruitmenttestapp.unittest.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

    private val analyticsHelper: AnalyticsHelper = mock()

    private val getAlbumDetailsUseCase: GetAlbumDetailsUseCase = mock()

    private val converter: AlbumConverter = mock()

    private val savedStateHandle = SavedStateHandle(initialState = mapOf("albumId" to ALBUM_ID))

    private fun createViewModel() = AlbumDetailsViewModel(
        savedStateHandle = savedStateHandle,
        analyticsHelper = analyticsHelper,
        getAlbumDetailsUseCase = getAlbumDetailsUseCase,
        converter = converter,
    )

    @Before
    fun setup() = runTest {
        whenever(getAlbumDetailsUseCase(id = ALBUM_ID)) doReturn Err(error = Throwable())
    }

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
    }

    @Test
    fun `should update ui state with Error when get album details use case return an error`() =
        runTest {
            // GIVEN
            whenever(getAlbumDetailsUseCase(id = ALBUM_ID)) doReturn Err(error = IllegalStateException())

            // WHEN
            val viewModel = createViewModel()
            advanceUntilIdle()

            // THEN
            Assert.assertEquals(
                AlbumDetailsUiState.Error(message = "An error occurred, retry later"),
                viewModel.uiState.value,
            )
        }

    @Test
    fun `should update ui state to Success when get albums use case return albums`() = runTest {
        // GIVEN
        val album = mock<Album>()
        val albumViewState = mock<AlbumViewState>()
        whenever(getAlbumDetailsUseCase(id = ALBUM_ID)) doReturn Ok(value = album)
        whenever(converter.convertToViewState(album = album)) doReturn albumViewState

        // WHEN
        val viewModel = createViewModel()
        advanceUntilIdle()

        // THEN
        Assert.assertEquals(
            AlbumDetailsUiState.Success(album = albumViewState),
            viewModel.uiState.value
        )
    }
}