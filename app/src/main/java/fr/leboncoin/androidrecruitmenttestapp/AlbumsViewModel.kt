package fr.leboncoin.androidrecruitmenttestapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.michaelbull.result.mapBoth
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.leboncoin.androidrecruitmenttestapp.domain.model.Album
import fr.leboncoin.androidrecruitmenttestapp.domain.repository.AlbumRepository
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(DelicateCoroutinesApi::class)
@HiltViewModel
class AlbumsViewModel @Inject constructor(
    private val repository: AlbumRepository,
) : ViewModel() {

    private val _albums = MutableSharedFlow<List<Album>>()
    val albums: SharedFlow<List<Album>> = _albums

    fun loadAlbums() {
        GlobalScope.launch {
            repository.getAlbums().mapBoth(
                success = {
                    _albums.emit(value = it)
                },
                failure = {
                    // TODO: Handle errors
                }
            )
        }
    }

    class Factory(
        private val repository: AlbumRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AlbumsViewModel(repository) as T
        }
    }
}