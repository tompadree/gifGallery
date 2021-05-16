package com.example.gifgallery.data.source.local

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.gifgallery.data.models.GifModelDataItem
import com.example.gifgallery.data.models.Result
import com.example.gifgallery.data.source.GifDataSource
import com.example.gifgallery.ui.gifview.GifGalleryViewModel.Companion.CURRENT_INDEX
import com.example.gifgallery.ui.gifview.GifGalleryViewModel.Companion.PAGE_LIMIT
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author Tomislav Curis
 */
class GifDataSourceLocal(
    private val dao: GifDAO,
    private val dispatchers: CoroutineDispatcher = Dispatchers.IO
) : GifDataSource {

    /*TODO it can be spiked to look only on remote results bypassing local*/
    override fun observeGifs(searchQuery: String): LiveData<Result<List<GifModelDataItem>>> {
        Log.e("TEST", " CURRENT_INDEX + PAGE_LIMIT= " + (CURRENT_INDEX + PAGE_LIMIT).toString())
        return dao.observeGifs("%$searchQuery%", 0, CURRENT_INDEX + PAGE_LIMIT)
            .map {
                Log.e("TEST", " CURRENT_INDEX + PAGE_LIMIT it-.size= " + it.size)// "%$searchQuery%", , 0, CURRENT_INDEX
                if(CURRENT_INDEX > it.size) CURRENT_INDEX = it.size
                if (searchQuery.isNotEmpty())
                    Result.Success(it)
                else
                    Result.Success(emptyList())
            }
    }



    override suspend fun saveGifs(gifs: List<GifModelDataItem>) =
        withContext(dispatchers) {
            dao.saveGifs(gifs)
        }

    override suspend fun getGifs(
        searchQuery: String,
        offset: Int,
        limit: Int
    ): Result<List<GifModelDataItem>> =
        withContext(dispatchers) {
            return@withContext try {
                Result.Success(dao.getGifs("%$searchQuery%",  offset, limit)) // "%$searchQuery%",  offset, limit
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override suspend fun deleteGifs() =
        withContext(dispatchers) {
            dao.deleteGifs()
        }
}