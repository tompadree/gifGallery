package com.example.gifgallery.utils.network


import java.io.IOException

class InternetConnectionException : IOException()

class NoConnectivityException : IOException() {
    override val message: String
        get() =
            "No network available, please check your WiFi or Data connection"
}

class NoInternetException() : IOException() {
    override val message: String
        get() =
            "No internet available, please check your connected WIFi or Data"
}