package com.example.newsonmap.ui.list

import android.graphics.Bitmap
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.newsonmap.databinding.ItemNewsBinding
import com.example.newsonmap.model.News

class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    fun bind(news : News){
        val binding = ItemNewsBinding.bind(itemView)

        binding.tvTitle.text = news.title
        binding.tvAddress.text = news.address
        binding.ivImage.setImageBitmap(news.imageBitmap)
    }
}