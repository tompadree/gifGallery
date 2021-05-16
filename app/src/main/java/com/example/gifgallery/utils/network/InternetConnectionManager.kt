package com.example.gifgallery.utils.network

import android.content.BroadcastReceiver
import android.view.View

/**
 * @author Tomislav Curis
 */
interface InternetConnectionManager {
    fun hasInternetConnection(): Boolean

    fun isInternetAvailable(mParentLayout: View) : BroadcastReceiver
}