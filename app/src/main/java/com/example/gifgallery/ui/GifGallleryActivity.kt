package com.example.gifgallery.ui

import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.example.gifgallery.R
import com.example.gifgallery.utils.network.InternetConnectionManager
import org.koin.android.ext.android.inject

class GifGallleryActivity : AppCompatActivity() {

    private val onDestinationChangedListener = this@GifGallleryActivity::onDestinationChanged
    private lateinit var appBarConfiguration: AppBarConfiguration

    private val internetConnectionManager: InternetConnectionManager by inject()
    private lateinit var internetReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gif_galllery)

        showSnackbar()
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.mainNavHost).navigateUp(appBarConfiguration) ||
                super.onSupportNavigateUp()
    }

    override fun onPause() {
        val navHostFragment = (supportFragmentManager.primaryNavigationFragment as NavHostFragment?)!!
        navHostFragment.navController.removeOnDestinationChangedListener(onDestinationChangedListener)
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        val navHostFragment = (supportFragmentManager.primaryNavigationFragment as NavHostFragment?)!!
        navHostFragment.navController.addOnDestinationChangedListener(onDestinationChangedListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(internetReceiver)
    }

    private fun onDestinationChanged(
            controller: NavController,
            destination: NavDestination,
            arguments: Bundle?) {

    }

    private fun showSnackbar() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        internetReceiver = internetConnectionManager.isInternetAvailable(findViewById(android.R.id.content))
        registerReceiver(internetReceiver, intentFilter)
    }

}