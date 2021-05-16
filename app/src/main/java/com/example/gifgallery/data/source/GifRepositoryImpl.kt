package com.example.gifgallery.data.source

import androidx.lifecycle.LiveData
import com.example.gifgallery.data.models.GifModelDataItem
import com.example.gifgallery.data.models.Result
import com.example.gifgallery.utils.wrapEspressoIdlingResource
import java.lang.Exception

/**
 * @author Tomislav Curis
 */
class GifRepositoryImpl(
    private val gifDataSourceLocal: GifDataSource,
    private val gifDataSourceRemote: GifDataSource) : GifRepository {

    override fun observeGifs(searchQuery: String): LiveData<Result<List<GifModelDataItem>>>  =
        wrapEspressoIdlingResource {
        gifDataSourceLocal.observeGifs(searchQuery)
    }

    override suspend fun saveGifs(gifs: List<GifModelDataItem>) =
        wrapEspressoIdlingResource {
        gifDataSourceLocal.saveGifs(gifs)
    }

    // it can be spiked to look only on remote results bypassing local
    override suspend fun getGifs(update: Boolean, searchQuery: String, offset: Int, limit: Int): Result<List<GifModelDataItem>> {
        if(update)
            try {
                updateGifsFromRemote(searchQuery, offset, limit)
            } catch (e: Exception) {
                return Result.Error(e)
            }
        return gifDataSourceLocal.getGifs(searchQuery, offset, limit)
    }

    private suspend fun updateGifsFromRemote(searchQuery: String, offset: Int, limit: Int) {
        wrapEspressoIdlingResource {
            val remoteGifs = gifDataSourceRemote.getGifs(searchQuery, offset, limit)
            if(remoteGifs is Result.Success) {
                gifDataSourceLocal.saveGifs(remoteGifs.data)
            } else if (remoteGifs is Result.Error) {
                throw remoteGifs.exception
            }
        }
    }
}