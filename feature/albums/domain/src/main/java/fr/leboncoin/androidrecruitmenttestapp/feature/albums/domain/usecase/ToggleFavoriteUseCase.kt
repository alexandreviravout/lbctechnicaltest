package fr.leboncoin.androidrecruitmenttestapp.feature.albums.domain.usecase

import com.github.michaelbull.result.Result
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.domain.repository.AlbumRepository
import javax.inject.Inject

/**
 * This class defines the use case to add or remove an album from favorites.
 *
 * @author alexandre.viravout
 */
class ToggleFavoriteUseCase @Inject constructor(
    private val repository: AlbumRepository,
) {

    suspend operator fun invoke(id: Int): Result<Boolean, Throwable> =
        repository.toggleFavorite(id = id)
}