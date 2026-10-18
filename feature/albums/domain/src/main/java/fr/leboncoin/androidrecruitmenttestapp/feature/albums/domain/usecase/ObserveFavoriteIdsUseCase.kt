package fr.leboncoin.androidrecruitmenttestapp.feature.albums.domain.usecase

import fr.leboncoin.androidrecruitmenttestapp.feature.albums.domain.repository.AlbumRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * This class defines the use case to observe favorite album ids.
 *
 * @author alexandre.viravout
 */
class ObserveFavoriteIdsUseCase @Inject constructor(
    private val repository: AlbumRepository,
) {

    operator fun invoke(): Flow<Set<Int>> = repository.observeFavoriteIds()
}