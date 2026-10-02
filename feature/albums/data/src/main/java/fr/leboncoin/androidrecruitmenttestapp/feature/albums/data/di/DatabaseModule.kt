package fr.leboncoin.androidrecruitmenttestapp.feature.albums.data.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.data.local.AlbumsDatabase
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.data.local.dao.AlbumDao
import javax.inject.Singleton

/**
 * This class defines database provider.
 *
 * @author alexandre.viravout
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AlbumsDatabase =
        Room.databaseBuilder(context, AlbumsDatabase::class.java, "albums.db")
            .build()

    @Provides
    fun provideAlbumDao(database: AlbumsDatabase): AlbumDao = database.albumDao()
}