package com.example.newsonmap.ui.list

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.newsonmap.R
import com.example.newsonmap.databinding.ItemNewsBinding
import com.example.newsonmap.model.News
import com.example.newsonmap.model.NewsType

class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(news: News) {
        val binding = ItemNewsBinding.bind(itemView)

        binding.tvTitle.text = news.title
        val authorText = "Author: ${news.authorName}"
        binding.tvAuthor.text = authorText
        binding.tvAddress.text = news.address
        if (news.imageBitmap != null){
            binding.ivImage.setImageBitmap(news.imageBitmap)
        }
        else{
            when (news.type) {
                NewsType.EVENT -> {
                    binding.ivImage.setImageResource(R.drawable.ic_event)
                }
                NewsType.AD -> {
                    binding.ivImage.setImageResource(R.drawable.ic_ad)
                }
                else -> {
                    binding.ivImage.setImageResource(R.drawable.ic_info)

                }
            }
        }
    }
}