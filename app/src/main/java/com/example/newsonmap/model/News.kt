package com.example.newsonmap.model

import android.graphics.Bitmap

data class News(
    var title: String = "",
    var description: String = "",
    var address: String = "",
    var latitude: String = "",
    var longitude: String = "",
    var timeCreated: String = "",
    var authorName: String = "",
    var imageBitmap: Bitmap? = null,
    var type: NewsType = NewsType.INFO
)