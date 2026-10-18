package fr.leboncoin.androidrecruitmenttestapp.feature.albums.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.data.local.dao.AlbumDao
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.data.local.dao.FavoriteDao
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.data.local.entity.AlbumEntity
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.data.local.entity.FavoriteEntity

/**
 * This class defines the albums database.
 *
 * @author alexandre.viravout
 */
@Database(
    entities = [AlbumEntity::class, FavoriteEntity::class],
    version = 2,
    exportSchema = false,
)
abstract class AlbumsDatabase : RoomDatabase() {
    abstract fun albumDao(): AlbumDao
    abstract fun favoriteDao(): FavoriteDao
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `favorites` (`id` INTEGER NOT NULL, PRIMARY KEY(`id`))")
    }
}