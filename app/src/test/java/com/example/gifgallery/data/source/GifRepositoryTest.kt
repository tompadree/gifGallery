package com.example.gifgallery.data.source

import com.example.gifgallery.utils.MainCoroutineRule
import com.google.common.truth.Truth
import com.google.common.truth.Truth.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.example.gifgallery.data.models.Result
import com.example.gifgallery.data.source.FakeDataSource.Companion.gifItems
import org.junit.Assert.assertEquals

/**
 * @author Tomislav Curis
 */

@ExperimentalCoroutinesApi
class GifRepositoryTest {

    // Dataset
    private val gifObject1 = gifItems[0]
    private val gifObject2 = gifItems[1]
    private val gifObject3 = gifItems[2]
    private val remoteGifs = listOf(gifObject3, gifObject1).sortedBy { it.id }
    private val localGifs = listOf(gifObject3, gifObject2).sortedBy { it.id }
    private val newGifs = listOf(gifObject1, gifObject2).sortedBy { it.id }

    private lateinit var gifDataSourceRemote: FakeDataSource
    private lateinit var gifDataSourceLocal: FakeDataSource

    private lateinit var repository: GifRepositoryImpl

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Before
    fun createRepository() {
        gifDataSourceLocal = FakeDataSource(localGifs.toMutableList())
        gifDataSourceRemote = FakeDataSource(remoteGifs.toMutableList())

        repository = GifRepositoryImpl(gifDataSourceLocal, gifDataSourceRemote)
    }

    @Test
    fun getGifs_emptyRepositoryAndUninitializedCache() = mainCoroutineRule.runBlockingTest {
        val emptySource = FakeDataSource()
        val tempRepository = GifRepositoryImpl(emptySource, emptySource)

        Truth.assertThat(tempRepository.getGifs(true, "1", 0, 100) is Result.Success)
                .isTrue()
    }

    @Test
    fun getGifs_gifCacheAfterFirstApiCall() = mainCoroutineRule.runBlockingTest {
        // false trigger is default
        val initial = repository.getGifs(false, "GifTest", 1, 100)

        gifDataSourceRemote.gifs = newGifs.toMutableList()

        val second = repository.getGifs(false, "GifTest", 1, 100)

        // Initial and second should match because no backend is called
        assertThat(second).isEqualTo(initial)
    }

    @Test
    fun getGifs_requestsAllGifsFromLocalDataSource() = mainCoroutineRule.runBlockingTest {
        // When gifs are requested from the giphy repository
        val gifs = repository.getGifs(false, "GifTest", 1, 100) as Result.Success

        // Then gifs are loaded from the local data source
        assertThat(gifs.data).isEqualTo(localGifs)
    }

    @Test
    fun getGifs_requestsAllGifsFromRemoteDataSource() = mainCoroutineRule.runBlockingTest {
        // When gifs are requested from the giphy repository
        val gifs = repository.getGifs(true, "GifTest", 1, 100) as Result.Success

        // Then gifs are loaded from the remote data source
        assertThat(gifs.data).isEqualTo(remoteGifs)
    }

    @Test
    fun saveGifs_savesToLocal() = mainCoroutineRule.runBlockingTest {
        // When gifs are requested from the giphy repository
        val gifs = repository.getGifs(true, "GifTest", 1, 100) as Result.Success

        // Save gifs
        repository.saveGifs(gifs.data)

        // Fetch them
        val gifsLocal = repository.getGifs(true, "GifTest", 1, 100) as Result.Success

        assertThat(gifs.data).isEqualTo(gifsLocal.data)
    }

    @Test
    fun getGifs_WithDirtyCache_gifsAreRetrievedFromRemote() = mainCoroutineRule.runBlockingTest {
        // First call returns from REMOTE
        val gifs = repository.getGifs(false, "GifTest", 1, 100) as Result.Success

        // Set a different list of gifs in REMOTE
        gifDataSourceRemote.gifs = newGifs.toMutableList()

        // But if gifs are cached, subsequent calls load from cache
        val cachedGifs = repository.getGifs(false, "GifTest", 1, 100) as Result.Success
        assertThat(cachedGifs).isEqualTo(gifs)

        // Now force remote loading
        val refreshedGifs = repository.getGifs(true, "GifTest", 1, 100) as Result.Success

        // gifs must be the recently updated in REMOTE
        assertThat(refreshedGifs.data).isEqualTo(newGifs)
    }

    @Test
    fun getGifs_remoteUnavailable_error() = mainCoroutineRule.runBlockingTest {
        // Make remote data source unavailable
        gifDataSourceRemote.gifs = null

        // Load gifs forcing remote load
        val gifs = repository.getGifs(true, "GifTest", 1, 100)

        // Result should be an error
        assertThat(gifs).isInstanceOf(Result.Error::class.java)
    }

    @Test
    fun getGifs_WithRemoteDataSourceUnavailable_gifsAreRetrievedFromLocal() =
            mainCoroutineRule.runBlockingTest {
                // When the remote data source is unavailable
                gifDataSourceRemote.gifs = null

                // The repository fetches from the local source
                assertThat((repository.getGifs(false, "GifTest", 1, 100) as Result.Success).data).isEqualTo(localGifs)
            }

    @Test
    fun getGifs_WithBothDataSourcesUnavailable_returnsError() = mainCoroutineRule.runBlockingTest {
        // When both sources are unavailable
        gifDataSourceRemote.gifs = null
        gifDataSourceLocal.gifs = null

        // The repository returns an error
        assertThat(repository.getGifs(false, "GifTest", 1, 100)).isInstanceOf(Result.Error::class.java)
    }

    @Test
    fun getGifs_refreshGifsFromRemoteDataSource() = mainCoroutineRule.runBlockingTest {
        // Initial state in db
        val initial = gifDataSourceLocal.gifs

        // Fetch from remote
        val remoteGifsTemp = repository.getGifs(true, "GifTest", 1, 100) as Result.Success

        assertEquals(remoteGifsTemp.data, remoteGifs)
        assertEquals(remoteGifsTemp.data, gifDataSourceLocal.gifs)
        assertThat(gifDataSourceLocal.gifs).isEqualTo(initial)
    }

    @Test
    fun getGifs_deleteGifs() = mainCoroutineRule.runBlockingTest {
        // Get gifs
        val initialGifs = repository.getGifs(false, "GifTest", 1, 100) as? Result.Success

        gifDataSourceLocal.deleteGifs()

        // Fetch after delete
        val afterDeleteGifs = repository.getGifs(false, "GifTest", 1, 100) as? Result.Success

        //check
        assertThat(initialGifs?.data).isNotEmpty()
        assertThat(afterDeleteGifs?.data).isEmpty()
    }
}