package com.example.gifgallery

import android.app.Application
import com.example.gifgallery.di.AppModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import com.example.gifgallery.di.DataModule
import com.example.gifgallery.di.RepoModule
import com.example.gifgallery.di.NetModule

/**
 * @author Tomislav Curis
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin()
    }

    private fun initKoin() {
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(listOf(AppModule, DataModule, RepoModule, NetModule))
        }
    }
}