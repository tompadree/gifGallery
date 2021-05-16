package com.example.gifgallery.data.models

/**
 * @author Tomislav Curis
 */
import com.google.gson.annotations.SerializedName

data class NetworkError(

    @SerializedName("message")
    val message: String,

    @SerializedName("meta")
    val meta: Meta

)

data class Meta(

    @SerializedName("status")
    val status: String,
    @SerializedName("msg")
    val msg: String
)