package com.example.gifgallery.data.source.remote.api

import com.example.gifgallery.data.models.GiphyResponse
import com.example.gifgallery.data.source.remote.api.APIConstants.Companion.API_URL_SEARCH
import com.example.gifgallery.data.source.remote.api.APIConstants.Companion.CONTENT_TYPE
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

/**
 * @author Tomislav Curis
 */
interface GifApi {

    @Headers(CONTENT_TYPE)
    @GET(API_URL_SEARCH)
    suspend fun searchGifs(@Query("api_key") api_key: String, @Query("q") q: String,
                           @Query("offset") offset: String, @Query("limit") limit: String,
                           @Query("rating") rating: String) : Response<GiphyResponse>
}