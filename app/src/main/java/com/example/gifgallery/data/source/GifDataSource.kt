package com.example.gifgallery.data.source

import androidx.lifecycle.LiveData
import com.example.gifgallery.data.models.GifModelDataItem
import com.example.gifgallery.data.models.Result

/**
 * @author Tomislav Curis
 */
interface GifDataSource {

    fun observeGifs(searchQuery: String): LiveData<Result<List<GifModelDataItem>>>

    suspend fun saveGifs(gifs: List<GifModelDataItem>)

    suspend fun getGifs(searchQuery: String, offset: Int, limit: Int): Result<List<GifModelDataItem>>

    suspend fun deleteGifs()

}