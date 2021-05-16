package com.example.gifgallery.data.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.gifgallery.data.models.GifModelDataItem
import com.example.gifgallery.data.models.Result
import kotlinx.coroutines.runBlocking
import java.lang.Exception

/**
 * @author Tomislav Curis
 */
class FakeRepository: GifRepository {

    var currentListGifs: List<GifModelDataItem> = mutableListOf()

    private var shouldReturnError = false

    private val observableGifs = MutableLiveData<Result<List<GifModelDataItem>>>()

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

    override fun observeGifs(searchQuery: String): LiveData<Result<List<GifModelDataItem>>> {
        runBlocking { observableGifs.value = Result.Success(currentListGifs) }
        return observableGifs
    }

    override suspend fun saveGifs(gifs: List<GifModelDataItem>) {
        (currentListGifs as ArrayList).clear()
        (currentListGifs as ArrayList).addAll(gifs)
    }

    override suspend fun getGifs(update: Boolean, searchQuery: String, offset: Int, limit: Int): Result<List<GifModelDataItem>> {
        if(shouldReturnError) {
            return Result.Error(Exception("Test exception"))
        }
        return Result.Success(currentListGifs)
    }
}