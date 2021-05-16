package com.example.gifgallery.koin

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import com.example.gifgallery.TestApp

class KoinTestRunner: AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader?, className: String?, context: Context?): Application {
        return super.newApplication(
                cl, TestApp::class.java.name, context
        )
    }
}