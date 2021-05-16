package com.example.gifgallery.ui.gifview

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.ImageView
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import com.example.gifgallery.R
import com.example.gifgallery.TestApp
import androidx.test.espresso.matcher.ViewMatchers.*
import com.example.gifgallery.data.source.FakeRepository
import com.example.gifgallery.data.source.GifRepository
import com.example.gifgallery.data.source.local.GifDAOTest.Companion.gifItemsIntegration
import com.example.gifgallery.utils.ViewIdlingResource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import org.mockito.Mockito

/**
 * @author Tomislav Curis
 */

@RunWith(AndroidJUnit4::class)
@MediumTest
@ExperimentalCoroutinesApi
class GifGalleryFragmentTest : KoinTest {

    // Use a fake repository to be injected
    private lateinit var repository: GifRepository

    private var gifs = listOf(gifItemsIntegration[0])

    private val viewModel : GifGalleryViewModel by inject()

    @Before
    fun initRepo() {

        repository = FakeRepository()

        val application = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as TestApp
        application.injectModule(module {
            single(override = true) { repository }
        })

        // Fill the db
        runBlocking {
//            val uri = Uri.parse("android.resource://com.example.picgalleryapp/drawable/test_image").toString()

            repository.saveGifs(gifs)
        }
    }

    @Test
    fun displayHomepageMessage() {
        // GIVEN - On the home screen
        launchFragment()

        // Check if message is shown
        onView(withText(R.string.search_something))
                .check(ViewAssertions.matches(isDisplayed()))
    }

    @Test
    fun displayGifs() {
        // GIVEN - On the home screen
        launchFragment()

        // Start search
        onView(withId(R.id.action_search)).perform(click())
        onView(withId(R.id.search_src_text)).perform(replaceText("g"))

        // Wait image to be shown
        idlingResourceWithId(R.id.gifItemImageView)

        onView(withId(R.id.gifItemImageView))
            .check(ViewAssertions.matches(isDisplayed()))
    }

    @Test
    fun displayGifsAndShowExpandedImage() {
        // GIVEN - On the home screen
        launchFragment()

        // Start search
        onView(withId(R.id.action_search)).perform(click())
        onView(withId(R.id.search_src_text)).perform(replaceText("g"))

        // Wait image to be shown
        idlingResourceWithId(R.id.gifItemImageView)
        onView(withId(R.id.gifItemImageView))
                .check(ViewAssertions.matches(isDisplayed()))

        // Click on image
        onView(withId(R.id.gifItemImageView)).perform(click())
        idlingResourceWithId(R.id.imageViewBig)

        onView(withId(R.id.imageViewBig))
                .check(ViewAssertions.matches(isDisplayed()))

    }

    // https://medium.com/@dbottillo/android-ui-test-espresso-matcher-for-imageview-1a28c832626f
    private fun <T> customMatcherForDrawable(imageId: Int): Matcher<T> {

        return object : BaseMatcher<T>() {
            override fun matches(item: Any?): Boolean {
                if (item !is ImageView)
                    return false

                val bitmap1 = getBitmap(item.drawable)
                val bitmap2 = getBitmap(item.resources.getDrawable(imageId, null))

                return bitmap1.sameAs(bitmap2)
            }

            override fun describeTo(description: Description?) {}


            fun getBitmap(drawable: Drawable): Bitmap {
                val bitmap = Bitmap.createBitmap(
                    drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888
                );
                val canvas = Canvas(bitmap);
                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);
                return bitmap;
            }

        }
    }

    private fun launchFragment() {
        // GIVEN - On the home screen
        val scenario = launchFragmentInContainer<GifGalleryFragment>(Bundle(), R.style.AppThemeNoActionBar)

        val navController = Mockito.mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }
    }

    private fun idlingResourceWithId(id: Int){
        val matcher = withId(id)
        val resource = ViewIdlingResource(matcher, isEnabled())
        try {
            IdlingRegistry.getInstance().register(resource)
            onView(matcher).check(ViewAssertions.matches(isEnabled()))

        } finally {
            IdlingRegistry.getInstance().unregister(resource)
        }
    }
}