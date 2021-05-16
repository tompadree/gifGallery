package com.example.gifgallery.di

import androidx.fragment.app.FragmentActivity
import androidx.room.Room
import com.example.gifgallery.BuildConfig
import com.example.gifgallery.data.source.GifDataSource
import com.example.gifgallery.data.source.GifRepository
import com.example.gifgallery.data.source.GifRepositoryImpl
import com.example.gifgallery.data.source.local.GifDataSourceLocal
import com.example.gifgallery.data.source.local.GifDatabase
import com.example.gifgallery.data.source.remote.api.APIConstants
import com.example.gifgallery.data.source.remote.api.GifApi
import com.example.gifgallery.data.source.remote.GifDataSourceRemote
import com.example.gifgallery.ui.gifview.GifGalleryViewModel
import com.example.gifgallery.utils.helpers.DialogManager
import com.example.gifgallery.utils.helpers.DialogManagerImpl
import com.example.gifgallery.utils.network.InternetConnectionManager
import com.example.gifgallery.utils.network.InternetConnectionManagerImpl
import com.example.gifgallery.utils.network.NullOnEmptyConverterFactory
import com.example.gifgallery.utils.network.ResponseInterceptor
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * @author Tomislav Curis
 */

val AppModule = module {
    factory { (activity: FragmentActivity) -> DialogManagerImpl(activity) as DialogManager }
}

val DataModule = module {

    single { Room.databaseBuilder(androidContext(), GifDatabase::class.java, "gif_db").build() }
    single  { get<GifDatabase>().getGifDAO() }

    single { Dispatchers.IO }

    single(named("local")) { GifDataSourceLocal(get(), get()) as GifDataSource }
    single(named("remote")) { GifDataSourceRemote(get()) as GifDataSource }
}

val RepoModule = module {
    single { GifRepositoryImpl(get(qualifier = named("local")),
            get(qualifier = named("remote"))) as GifRepository
    }

    viewModel { GifGalleryViewModel(get(), get()) }
}

val NetModule = module {

    single { InternetConnectionManagerImpl() as InternetConnectionManager }

    single {

        OkHttpClient.Builder()
                .connectTimeout(40, TimeUnit.SECONDS)
//            .addInterceptor(NetworkExceptionInterceptor())
                .addInterceptor(ResponseInterceptor(get())).apply {
                    if (BuildConfig.DEBUG) {
                        val loggingInterceptor = HttpLoggingInterceptor()
                        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                        addInterceptor(loggingInterceptor)
                    }
                }
                .build()
    }

    single {
        (Retrofit.Builder()
                .baseUrl(APIConstants.BASE_URL)
                .client(get())
                .addConverterFactory(NullOnEmptyConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(GifApi::class.java)) as GifApi
    }
}