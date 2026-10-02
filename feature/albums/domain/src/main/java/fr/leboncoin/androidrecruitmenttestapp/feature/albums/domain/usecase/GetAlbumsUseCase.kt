package fr.leboncoin.androidrecruitmenttestapp.feature.albums.domain.usecase

import com.github.michaelbull.result.Result
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.domain.model.Album
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.domain.repository.AlbumRepository
import javax.inject.Inject

/**
 * This class defines get albums use case.
 *
 * @author alexandre.viravout
 */
class GetAlbumsUseCase @Inject constructor(
    private val repository: AlbumRepository,
) {

    suspend operator fun invoke(forceRefresh: Boolean): Result<List<Album>, Throwable> =
        repository.getAlbums(forceRefresh = forceRefresh)
}