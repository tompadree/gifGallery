package com.example.gifgallery

import android.app.Application
import com.example.gifgallery.di.AppModule
import com.example.gifgallery.di.DataModule
import com.example.gifgallery.di.NetModule
import com.example.gifgallery.di.RepoModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.module.Module

/**
 * @author Tomislav Curis
 */
class TestApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@TestApp)
            modules(listOf(AppModule, DataModule, NetModule, RepoModule))
        }
    }

    internal fun injectModule(module: Module) {
        loadKoinModules(module)
    }
}