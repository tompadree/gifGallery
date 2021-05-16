package com.example.gifgallery.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import java.io.Serializable

/**
 * @author Tomislav Curis
 */

data class GiphyResponse(



        @SerializedName("data")
        val data: List<GifModelDataItem>
)


@Entity(tableName = "gifs")
data class GifModelDataItem(

        @PrimaryKey
        @SerializedName("id")
        val id: String,

        @SerializedName("title")
        val title: String,

        @SerializedName("images")
        val images: ImagesObject

        ) : Serializable

data class ImagesObject(

        @SerializedName("fixed_width_small")
        val fixedWithSmall: ImageObjectItem
)

data class ImageObjectItem(

        @SerializedName("url")
        val url: String? = ""


//    "height": "53",
//    "width": "100",
//    "size": "50549",
//    "url": "https://media3.giphy.com/media/3o7bub82dBk0N45zPy/100w.gif?cid=29307de45h950kbea5nfb5pmt7b5unnrw6w5bp80tmftjvl6&rid=100w.gif&ct=g",
//    "mp4_size": "9765",
//    "mp4": "https://media3.giphy.com/media/3o7bub82dBk0N45zPy/100w.mp4?cid=29307de45h950kbea5nfb5pmt7b5unnrw6w5bp80tmftjvl6&rid=100w.mp4&ct=g",
//    "webp_size": "29900",
//    "webp": "https://media3.giphy.com/media/3o7bub82dBk0N45zPy/100w.webp?cid=29307de45h950kbea5nfb5pmt7b5unnrw6w5bp80tmftjvl6&rid=100w.webp&ct=g"
)



class DataConverter {

        companion object {
                @TypeConverter
                @JvmStatic
                fun stringToData(value: String): ArrayList<GifModelDataItem> {
                        val listType = object : TypeToken<List<String>>() {}.type
                        return Gson().fromJson(value, listType)
                }

                @TypeConverter
                @JvmStatic
                fun fromDataToString(list: List<GifModelDataItem>): String = Gson().toJson(list)

        }
}

class ImagesObjectConverter {

        @TypeConverter
        fun stringToImagesObject(imagesObject: String): ImagesObject? =
                Gson().fromJson(imagesObject, ImagesObject::class.java)

        @TypeConverter
        fun fromImagesObjectToString(imagesObject: ImagesObject): String? = Gson().toJson(imagesObject)

}

class ImageObjectItemConverter {

        @TypeConverter
        fun stringToImageObjectItem(imageObjectItem: String): ImageObjectItem? =
                Gson().fromJson(imageObjectItem, ImageObjectItem::class.java)

        @TypeConverter
        fun fromImageObjectItemToString(imageObjectItem: ImageObjectItem): String? = Gson().toJson(imageObjectItem)

}