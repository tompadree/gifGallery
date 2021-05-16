package com.example.gifgallery.ui.gifview

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.gifgallery.data.source.FakeDataSource.Companion.gifItems
import com.example.gifgallery.data.source.FakeRepository
import com.example.gifgallery.di.DataModule
import com.example.gifgallery.di.NetModule
import com.example.gifgallery.getOrAwaitValue
import com.example.gifgallery.observeForTesting
import com.example.gifgallery.utils.MainCoroutineRule
import com.example.gifgallery.utils.network.InternetConnectionManager
import com.google.common.truth.Truth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject

/**
 * @author Tomislav Curis
 */

@ExperimentalCoroutinesApi
class GifGalleryViewModelTest : KoinTest {

    // What is testing
    private lateinit var gifGalleryViewModel: GifGalleryViewModel

    // Use a fake repository to be injected into the viewmodel
    private lateinit var repository: FakeRepository

    // Rule for koin injection
    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(listOf(DataModule, NetModule))
    }

    private val internetConnectionManager: InternetConnectionManager by inject()

    // Set the main coroutines dispatcher for unit testing.
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel() {
        repository = FakeRepository()

        repository.currentListGifs = mutableListOf(gifItems[0], gifItems[1], gifItems[2])

        gifGalleryViewModel = GifGalleryViewModel(repository, internetConnectionManager)
    }

    @Test
    fun loadAllGifsToView() {
        // Pause dispatcher so we can verify initial values
        mainCoroutineRule.pauseDispatcher()

        // Trigger loading of gifs
        gifGalleryViewModel._currentSearch.set("GifTest")
        gifGalleryViewModel.refresh(true)

        // Observe the items to keep LiveData emitting
        gifGalleryViewModel.items.observeForTesting {

            // Loding
            Truth.assertThat(gifGalleryViewModel.dataLoading.getOrAwaitValue()).isTrue()

            // Execute pending coroutines actions
            mainCoroutineRule.resumeDispatcher()

            // loading is done
            Truth.assertThat(gifGalleryViewModel.dataLoading.getOrAwaitValue()).isFalse()

            // And data correctly loaded
            Truth.assertThat(gifGalleryViewModel.items.getOrAwaitValue()).hasSize(3)
        }
    }

    @Test
    fun fetchingGifsGetError() {
        // Pause dispatcher so we can verify initial values
        mainCoroutineRule.pauseDispatcher()

        // Set gif return error
        repository.setReturnError(true)

        // StartFetching
        gifGalleryViewModel._currentSearch.set("GifTest")
        gifGalleryViewModel.refresh(true)

        // Observe the items to keep LiveData emitting
        gifGalleryViewModel.items.observeForTesting {

            // Loding
            Truth.assertThat(gifGalleryViewModel.dataLoading.getOrAwaitValue()).isTrue()

            // Execute pending coroutines actions
            mainCoroutineRule.resumeDispatcher()

            // loading is done
            Truth.assertThat(gifGalleryViewModel.dataLoading.getOrAwaitValue()).isFalse()

            // If isDataLoadingError response was error
            Truth.assertThat(gifGalleryViewModel.isDataLoadingError.value).isEqualTo(true)
        }
    }
}

