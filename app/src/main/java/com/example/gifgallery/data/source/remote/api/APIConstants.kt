package com.example.gifgallery.data.source.remote.api

/**
 * @author Tomislav Curis
 */


interface APIConstants {

    companion object {

        const val BASE_URL = "https://api.giphy.com"

        const val API_URL_SEARCH = "/v1/gifs/search"

        const val CONTENT_TYPE = "Content-Type: application/json; charset=utf-8"

        const val GIPHY_RATING_LEVEL = "pg"

    }
}