package com.example.gifgallery.data.source

import androidx.lifecycle.LiveData
import com.example.gifgallery.data.models.GifModelDataItem
import com.example.gifgallery.data.models.ImageObjectItem
import com.example.gifgallery.data.models.ImagesObject
import java.lang.Exception
import com.example.gifgallery.data.models.Result

/**
 * @author Tomislav Curis
 */
class FakeDataSource(
        var gifs: MutableList<GifModelDataItem>? = mutableListOf()) : GifDataSource {

    companion object {
        val gifItems = arrayListOf<GifModelDataItem>(
            GifModelDataItem("1234", "GifTest1", ImagesObject(ImageObjectItem(("www.someapi.com")))),
            GifModelDataItem("2345", "GifTest2", ImagesObject(ImageObjectItem(("www.someapi.com")))),
            GifModelDataItem("3456", "GifTest3", ImagesObject(ImageObjectItem(("www.someapi.com"))))
        )
    }

    override fun observeGifs(searchQuery: String): LiveData<Result<List<GifModelDataItem>>> {
        TODO("Not yet implemented")
    }

    override suspend fun saveGifs(gifs: List<GifModelDataItem>) {
        this.gifs?.clear()
        this.gifs?.addAll(gifs)
    }

    override suspend fun getGifs(searchQuery: String, offset: Int, limit: Int): Result<List<GifModelDataItem>> {
        gifs?.let { return  Result.Success(ArrayList(it)) }
        return Result.Error(Exception("Gifs not found"))
    }

    override suspend fun deleteGifs() {
        gifs?.clear()
    }
}

