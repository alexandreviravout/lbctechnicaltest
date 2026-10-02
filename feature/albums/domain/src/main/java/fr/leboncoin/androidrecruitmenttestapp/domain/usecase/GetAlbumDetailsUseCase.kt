package fr.leboncoin.androidrecruitmenttestapp.domain.usecase

import com.github.michaelbull.result.Result
import fr.leboncoin.androidrecruitmenttestapp.domain.model.Album
import fr.leboncoin.androidrecruitmenttestapp.domain.repository.AlbumRepository
import javax.inject.Inject

/**
 * This class defines the use case to get an album.
 *
 * @author alexandre.viravout
 */
class GetAlbumDetailsUseCase @Inject constructor(
    private val repository: AlbumRepository,
) {

    suspend operator fun invoke(id: Int): Result<Album, Throwable> =
        repository.getAlbumDetails(id = id)
}