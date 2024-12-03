package com.nafis.moneylaundry.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nafis.moneylaundry.databinding.SelectImageRowBinding
import com.nafis.moneylaundry.models.SelectImageModel

class SelectImageAdapter(
    private val images: List<SelectImageModel>,
    private val onItemSelected: (SelectImageModel) -> Unit
) : RecyclerView.Adapter<SelectImageAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = SelectImageRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val image = images[position]
        holder.bind(image)
    }

    override fun getItemCount() = images.size

    inner class ImageViewHolder(private val binding: SelectImageRowBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(image: SelectImageModel) {
            binding.ivPaketLaundry.setImageResource(image.photo)
            binding.root.setOnClickListener {
                onItemSelected(image) // Mengirimkan seluruh objek SelectImageModel
            }
        }
    }
}

