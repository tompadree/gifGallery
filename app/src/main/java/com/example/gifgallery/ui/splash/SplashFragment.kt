package com.example.gifgallery.ui.splash

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import com.example.gifgallery.R
import com.example.gifgallery.utils.delay

/**
 * @author Tomislav Curis
 */

class SplashFragment : Fragment() {

    private val SPLASH_DISPLAY_LENGTH : Long = 2000

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        launchGit()
    }

    private fun launchGit() {
        delay(SPLASH_DISPLAY_LENGTH) {
            navigateToGitAccount()
        }
    }

    private fun navigateToGitAccount() {
        val nc = NavHostFragment.findNavController(this)
        nc.navigate(SplashFragmentDirections.actionSplashFragmentToGifGalleryFragment())
    }
}