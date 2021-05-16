package com.example.gifgallery.ui.gifview

import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gifgallery.R
import com.example.gifgallery.data.models.GifModelDataItem

/**
 * @author Tomislav Curis
 */

@BindingAdapter("app:items")
fun setItems(listView: RecyclerView, items: List<GifModelDataItem>?) {
    if (items == null) return

    (listView.adapter as GifGalleryAdapter).submitList(items)
}

@BindingAdapter("imageUrl")
fun setGif(imageView: ImageView, url: String) {
    try {
        Glide
            .with(imageView.context).load(url)
            .thumbnail(Glide.with(imageView.context).load(R.drawable.progress_gif))
            .fitCenter()
            .into(imageView)
        imageView.clearFocus()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

@BindingAdapter("app:searchResult")
fun fetchSearchResults(gitResultToolbar: Toolbar, queryListener: OnSearchTermListener) {
    val DUMMY_SEARCH = ""

    gitResultToolbar.inflateMenu(R.menu.menu_gif_gallery)

    val searchItem = gitResultToolbar.menu.findItem(R.id.action_search)
    val searchView = searchItem.actionView as SearchView

    var lastText = ""

    searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

        override fun onQueryTextChange(newText: String): Boolean {
            if (newText != lastText) {
                if (newText == "")
                    queryListener.onQuery(DUMMY_SEARCH)
                else
                    queryListener.onQuery(newText)
                lastText = newText
            }
            return false
        }

        override fun onQueryTextSubmit(query: String): Boolean {
            if (query.isNotEmpty()) {

                if (query == "")
                    queryListener.onQuery(DUMMY_SEARCH)
                else
                    queryListener.onQuery(query)
            }
            return false
        }
    })
}

interface OnSearchTermListener {
    fun onQuery(query: String)
}