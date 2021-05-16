package com.example.gifgallery.data.source.local

import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.gifgallery.data.models.GifModelDataItem
import com.example.gifgallery.data.models.ImageObjectItem
import com.example.gifgallery.data.models.ImagesObject
import org.hamcrest.CoreMatchers.`is`
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import com.google.common.truth.Truth.assertThat
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
@SmallTest
class GifDAOTest {

    private lateinit var database: GifDatabase
    private lateinit var gifDAO: GifDAO

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDB () {
        // Using an in-memory database so that the information stored here disappears when the
        // process is killed.
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), GifDatabase::class.java).build()
        gifDAO = database.getGifDAO()
    }

    companion object {
        val uri = Uri.parse("android.resource://com.example.picgalleryapp/drawable/test_image").toString()
        val gifItemsIntegration = arrayListOf<GifModelDataItem>(
                GifModelDataItem("1234", "GifTest1", ImagesObject(ImageObjectItem((uri)))),
                GifModelDataItem("2345", "GifTest2", ImagesObject(ImageObjectItem((uri)))),
                GifModelDataItem("3456", "GifTest3", ImagesObject(ImageObjectItem((uri))))
        )
    }

    @After
    fun closeDB() = database.close()

    private var gifs = listOf(gifItemsIntegration[0], gifItemsIntegration[1])

    @Test
    fun insertGifsAndGet() = runBlockingTest {
        // Insert a gifObj
        gifDAO.saveGifs(gifs)

        // retrieve list
        val gifsTemp = gifDAO.getGifs("%%", 0,100)

        assertThat(gifsTemp[0].id).isEqualTo(gifs[0].id)
        assertThat(gifsTemp[0].title).isEqualTo(gifs[0].title)
        assertThat(gifsTemp[1].id).isEqualTo(gifs[1].id)
        assertThat(gifsTemp[1].title).isEqualTo(gifs[1].title)
    }

    @Test
    fun insertGifsAndGetByGifTitleName() = runBlockingTest {
        // Insert a gifObj
        gifDAO.saveGifs(gifs)

        // retrieve list
        val gifsTemp = gifDAO.getGifs("%GifTest%", 0,100)

        assertThat(gifsTemp[0].id).isEqualTo(gifs[0].id)
        assertThat(gifsTemp[0].title).isEqualTo(gifs[0].title)
        assertThat(gifsTemp[1].id).isEqualTo(gifs[1].id)
        assertThat(gifsTemp[1].title).isEqualTo(gifs[1].title)
    }

    @Test
    fun insertGifsReplacesOnConflict() = runBlockingTest {
        // Insert gifs
        gifDAO.saveGifs(gifs)

        // When a gifs with the same ids are inserted
        val newGifs =
                listOf(GifModelDataItem("1234", "GifTest1", ImagesObject(ImageObjectItem("www.gif1.com"))),
                        GifModelDataItem("2345", "GifTest2", ImagesObject(ImageObjectItem("www.gif2.com"))))

        gifDAO.saveGifs(newGifs)

        // retrieve list
        val gifsTemp = gifDAO.getGifs("%GifTest%",0,100)


        assertThat(gifsTemp[0].id, `is` (gifs[0].id))
        assertThat(gifsTemp[0].title, `is` (gifs[0].title))
        assertThat(gifsTemp[1].id, `is` (gifs[1].id))
        assertThat(gifsTemp[1].title, `is` (gifs[1].title))
    }

    @Test
    fun deleteGifs() = runBlockingTest {
        // Insert gifs
        gifDAO.saveGifs(gifs)

        // Delete gifs
        gifDAO.deleteGifs()

        // retrieve gifs
        val gifsTemp = gifDAO.getGifs("%GifTest%",0,100)

        assertThat(gifsTemp.isEmpty(), `is`(true))
    }
}