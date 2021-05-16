package com.example.gifgallery.data.source.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gifgallery.data.models.GifModelDataItem

/**
 * @author Tomislav Curis
 */

@Dao
interface GifDAO {

    /**
     * Observes list of gifs.
     *
     * @return all gifs.
     */
    @Query("SELECT * FROM gifs WHERE title LIKE :searchQuery LIMIT :limit OFFSET :offset")
    fun observeGifs(searchQuery: String, offset: Int, limit: Int): LiveData<List<GifModelDataItem>>

    /**
     * Delete all gifs.
     */
    @Query("DELETE FROM gifs")
    suspend fun deleteGifs()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveGifs(gifs: List<GifModelDataItem>) // : LongArray

    @Query("SELECT * FROM gifs WHERE title LIKE :searchQuery LIMIT :limit OFFSET :offset")
    fun getGifs(searchQuery: String, offset: Int, limit: Int): List<GifModelDataItem>

}