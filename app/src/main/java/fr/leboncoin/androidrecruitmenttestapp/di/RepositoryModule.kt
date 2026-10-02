package fr.leboncoin.androidrecruitmenttestapp.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.data.repository.AlbumDataRepository
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.domain.repository.AlbumRepository
import javax.inject.Singleton

/**
 * This abstract class link AlbumRepository with AlbumDataRepository.
 *
 * @author alexandre.viravout
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAlbumRepository(impl: AlbumDataRepository): AlbumRepository
}