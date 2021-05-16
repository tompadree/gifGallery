package com.example.gifgallery.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.gifgallery.data.models.DataConverter
import com.example.gifgallery.data.models.GifModelDataItem
import com.example.gifgallery.data.models.ImageObjectItemConverter
import com.example.gifgallery.data.models.ImagesObjectConverter

/**
 * @author Tomislav Curis
 */

@Database(entities = [GifModelDataItem::class], version = 1, exportSchema = false)
@TypeConverters(DataConverter::class, ImagesObjectConverter::class, ImageObjectItemConverter::class)
abstract class GifDatabase : RoomDatabase() {
    abstract fun getGifDAO() : GifDAO
}