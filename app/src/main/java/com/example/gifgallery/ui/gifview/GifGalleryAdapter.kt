package com.example.gifgallery.ui.gifview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.gifgallery.data.models.GifModelDataItem
import com.example.gifgallery.databinding.ItemGifBinding
import kotlinx.android.extensions.LayoutContainer

/**
 * @author Tomislav Curis
 */
class GifGalleryAdapter(private val viewModel: GifGalleryViewModel)
    : ListAdapter<GifModelDataItem, GifViewHolder>(GifGalleryDiffUtil()) {

    override fun onBindViewHolder(holder: GifViewHolder, position: Int) {
        holder.bind(viewModel, getItem(position), position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GifViewHolder {
        return GifViewHolder.from(parent)
    }
}

class GifGalleryDiffUtil : DiffUtil.ItemCallback<GifModelDataItem>() {
    override fun areContentsTheSame(oldItem: GifModelDataItem, newItem: GifModelDataItem): Boolean {
        return oldItem.images == newItem.images
    }

    override fun areItemsTheSame(oldItem: GifModelDataItem, newItem: GifModelDataItem): Boolean {
        return oldItem.id == newItem.id
    }
}

class GifViewHolder private constructor(val binding: ItemGifBinding)
    : RecyclerView.ViewHolder(binding.root), LayoutContainer {

    override val containerView = binding.root

    companion object {
        fun from(parent: ViewGroup): GifViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemGifBinding.inflate(layoutInflater, parent, false)

            return GifViewHolder(binding)
        }
    }

    fun bind(viewModel: GifGalleryViewModel, gifModelDataItem: GifModelDataItem, position: Int) {
        binding.viewModel = viewModel
        binding.gifModelDataItem = gifModelDataItem
        binding.executePendingBindings()
    }
}