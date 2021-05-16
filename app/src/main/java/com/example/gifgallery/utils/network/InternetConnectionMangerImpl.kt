package com.example.gifgallery.utils.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.NetworkInfo
import android.os.Parcelable
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.gifgallery.R
import com.google.android.material.snackbar.Snackbar

/**
 * @author Tomislav Curis
 */

class InternetConnectionManagerImpl : InternetConnectionManager {

    var isInternetOn = true

    override fun hasInternetConnection(): Boolean {
        return isInternetOn
    }


    override fun isInternetAvailable(mParentLayout: View): BroadcastReceiver {

        val noIntSnaBar = Snackbar.make(
            mParentLayout,
            R.string.no_internet_connection_text_2,
            Snackbar.LENGTH_INDEFINITE
        )

        val textView = noIntSnaBar.view.findViewById(R.id.snackbar_text) as TextView
        textView.setTextColor(ContextCompat.getColor(mParentLayout.context, R.color.white))
        noIntSnaBar.setBackgroundTint(ContextCompat.getColor(mParentLayout.context,R.color.black))

        return object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {

                val extras = intent.extras
                val info = extras!!.getParcelable<Parcelable>("networkInfo") as NetworkInfo

                val state = info.state

                isInternetOn = if (state == NetworkInfo.State.CONNECTED) {
                    noIntSnaBar.dismiss()
                    true
                } else {
                    noIntSnaBar.show()
                    false
                }
            }
        }
    }
}