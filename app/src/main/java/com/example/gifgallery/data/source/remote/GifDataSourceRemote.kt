package com.example.gifgallery.data.source.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.gifgallery.BuildConfig
import com.example.gifgallery.data.models.GifModelDataItem
import com.example.gifgallery.data.source.GifDataSource
import com.example.gifgallery.data.models.Result
import com.example.gifgallery.data.source.remote.api.APIConstants.Companion.GIPHY_RATING_LEVEL
import com.example.gifgallery.data.source.remote.api.GifApi
import java.io.IOException

/**
 * @author Tomislav Curis
 */
class GifDataSourceRemote(private val gifApi: GifApi) : GifDataSource {

    private val observableGifs = MutableLiveData<Result<List<GifModelDataItem>>>()

    override fun observeGifs(searchQuery: String): LiveData<Result<List<GifModelDataItem>>> = observableGifs

    override suspend fun saveGifs(gifs: List<GifModelDataItem>) {
        TODO("Not yet implemented")
    }

    override suspend fun getGifs(searchQuery: String, offset: Int, limit: Int): Result<List<GifModelDataItem>> {
        val response =
            gifApi.searchGifs(BuildConfig.GIPHY_API_KEY, searchQuery, offset.toString(), limit.toString(), GIPHY_RATING_LEVEL)
        if (response.isSuccessful) {
            val body = response.body()
            if (response.body() != null) {
                val result = Result.Success(body!!.data)
                observableGifs.value = result
                return result
            }
        }
        return Result.Error(IOException("Error loading data " + "${response.code()} ${response.message()}"))
    }

    override suspend fun deleteGifs() {
        TODO("Not yet implemented")
    }
}