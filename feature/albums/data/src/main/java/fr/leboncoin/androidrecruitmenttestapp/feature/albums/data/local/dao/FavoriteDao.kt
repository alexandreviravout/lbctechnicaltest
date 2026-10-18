package fr.leboncoin.androidrecruitmenttestapp.feature.albums.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import fr.leboncoin.androidrecruitmenttestapp.feature.albums.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

/**
 * This class defines favorite dao for database.
 *
 * @author alexandre.viravout
 */
@Dao
interface FavoriteDao {

    @Query("SELECT id FROM favorites")
    fun observeFavoriteIds(): Flow<List<Int>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE id = :id)")
    suspend fun isFavorite(id: Int): Boolean

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE id = :id")
    suspend fun delete(id: Int)

    /** Adds or removes the album from favorites and returns the new state. */
    @Transaction
    suspend fun toggle(id: Int): Boolean =
        if (isFavorite(id = id)) {
            delete(id = id)
            false
        } else {
            insert(favorite = FavoriteEntity(id = id))
            true
        }
}