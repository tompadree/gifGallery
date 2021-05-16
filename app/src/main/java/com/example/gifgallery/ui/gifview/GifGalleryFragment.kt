package com.example.gifgallery.ui.gifview

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.gifgallery.R
import com.example.gifgallery.databinding.GifGalleryFragmentBinding
import com.example.gifgallery.ui.BindingFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.example.gifgallery.utils.observe
import kotlinx.android.synthetic.main.gif_gallery_fragment.*


class GifGalleryFragment : BindingFragment<GifGalleryFragmentBinding>() {

    override val layoutId = R.layout.gif_gallery_fragment

    private val viewModel: GifGalleryViewModel by viewModel()

    private lateinit var gifAdapter: GifGalleryAdapter

    override fun onViewCreated() {
        super.onViewCreated()

        binding.viewModel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

        setupObservers()
        setupRV()
//        initVM()
//
    }

    override fun onPause() {
        super.onPause()
        (context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(activity?.currentFocus?.windowToken ?: return, 0)
    }

    private fun initVM() {
//        viewModel._currentSearch.set("a")
        viewModel.refresh(false)
    }

    private fun setupObservers() {
        observeError(viewModel.error)

        viewModel.isDataLoadingError.observe(this) {
            it?.let {
                gifsGallerySwipeLayout.isEnabled = it
                gifsGallerySwipeLayout.isRefreshing = it
            }
        }
        viewModel.itemClicked.observe(this){
            itemClicked(it!!)
        }
    }

    private fun itemClicked(url: String) {
        showBigImageDialog(url)
    }

    private fun setupRV() {
        gifAdapter = GifGalleryAdapter(viewModel)

        with(gifGalleryFragRv) {
            val layoutManagerRv = GridLayoutManager(context,2)
            layoutManager = layoutManagerRv
            adapter = gifAdapter
            (this.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

            // Set the number of offscreen views to retain before adding them
            // to the potentially shared recycled view pool
//            setItemViewCacheSize(2000)

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val totalItemCount = layoutManagerRv.itemCount
                    val lastVisibleItem = layoutManagerRv.findLastVisibleItemPosition()

                    if (!gifsGallerySwipeLayout.isRefreshing)
                        viewModel.listScrolled(lastVisibleItem, totalItemCount)
                }
            })
        }
    }

}