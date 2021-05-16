package com.example.gifgallery.data.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.gifgallery.data.models.GifModelDataItem
import com.example.gifgallery.data.models.ImageObjectItem
import com.example.gifgallery.data.models.ImagesObject
import com.example.gifgallery.data.models.Result
import com.example.gifgallery.data.source.local.GifDAOTest.Companion.gifItemsIntegration
import com.example.gifgallery.utils.MainCoroutineRule
import com.google.common.truth.Truth.assertThat
import org.hamcrest.MatcherAssert.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * @author Tomislav Curis
 */

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class GifDataSourceLocalTest {

    private lateinit var database: GifDatabase
    private lateinit var localDataSource: GifDataSourceLocal

    // Set the main coroutines dispatcher for unit testing.
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDB () {
        // Using an in-memory database so that the information stored here disappears when the
        // process is killed.
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), GifDatabase::class.java).build()
        localDataSource = GifDataSourceLocal(database.getGifDAO(), Dispatchers.Main) // It has to be on Main
    }

    @After
    fun closeDB() = database.close()

    private var gifs = listOf(gifItemsIntegration[0], gifItemsIntegration[1])

    @Test
    fun insertGifsAndGet() = runBlockingTest {
        // Insert a gifObj
        localDataSource.saveGifs(gifs)

        // retrieve list
        val gifsTemp = localDataSource.getGifs("%%", 0,100) as Result.Success

        assertThat(gifsTemp.data[0].id).isEqualTo(gifs[0].id)
        assertThat(gifsTemp.data[0].title).isEqualTo(gifs[0].title)
        assertThat(gifsTemp.data[1].id).isEqualTo(gifs[1].id)
        assertThat(gifsTemp.data[1].title).isEqualTo(gifs[1].title)
    }

    @Test
    fun insertGifsAndGetByGifTitleName() = runBlockingTest {
        // Insert a gifObj
        localDataSource.saveGifs(gifs)

        // retrieve list
        val gifsTemp = localDataSource.getGifs("%GifTest%", 0,100) as Result.Success

        assertThat(gifsTemp.data[0].id).isEqualTo(gifs[0].id)
        assertThat(gifsTemp.data[0].title).isEqualTo(gifs[0].title)
        assertThat(gifsTemp.data[1].id).isEqualTo(gifs[1].id)
        assertThat(gifsTemp.data[1].title).isEqualTo(gifs[1].title)
    }

    @Test
    fun insertGifsReplacesOnConflict() = runBlockingTest {
        // Insert gifs
        localDataSource.saveGifs(gifs)

        // When a gifs with the same ids are inserted
        val newGifs =
                listOf(GifModelDataItem("1234", "GifTest1", ImagesObject(ImageObjectItem("www.gif1.com"))),
                        GifModelDataItem("2345", "GifTest2", ImagesObject(ImageObjectItem("www.gif2.com"))))

        localDataSource.saveGifs(newGifs)

        // retrieve list
        val gifsTemp = localDataSource.getGifs("%GifTest%",0,100) as Result.Success


        assertThat(gifsTemp.data[0].id, CoreMatchers.`is` (gifs[0].id))
        assertThat(gifsTemp.data[0].title, CoreMatchers.`is` (gifs[0].title))
        assertThat(gifsTemp.data[1].id, CoreMatchers.`is` (gifs[1].id))
        assertThat(gifsTemp.data[1].title, CoreMatchers.`is` (gifs[1].title))
    }
}