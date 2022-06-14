package com.example.newsonmap.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.newsonmap.R
import com.example.newsonmap.model.News
import com.google.type.LatLng

class NewsAdapter : RecyclerView.Adapter<NewsViewHolder>(){
    private val newsList = mutableListOf<News>()
    var onNewsClickListener: OnNewsClickListener? = null

    fun setNews(news: List<News>){
        this.newsList.clear()
        this.newsList.addAll(news)
        this.notifyDataSetChanged()
    }

    fun addNews(news: News){
        this.newsList.add(news)
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val news = newsList[position]
        holder.bind(news)

        onNewsClickListener?.let{ listener ->
//            val latLng = LatLng(news.latitude.toDouble(), news.longitude.toDouble())
            val latLng = com.google.android.gms.maps.model.LatLng(news.latitude.toDouble(), news.longitude.toDouble())
//            val latLngBuilder = LatLng.newBuilder().setLatitude(news.latitude.toDouble()).setLongitude(news.longitude.toDouble())
            holder.itemView.setOnClickListener{listener.onNewsClick(latLng)}
        }
    }

    override fun getItemCount(): Int {
        return newsList.count()
    }
}