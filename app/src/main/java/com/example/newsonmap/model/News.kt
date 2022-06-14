package com.example.newsonmap.model

import android.graphics.Bitmap

data class News(
    var title: String = "",
    var description: String = "",
    var address: String = "",
    var latitude: String = "",
    var longitude: String = "",
    var time_created: String = "",
    var author: String = "",
    var imageBitmap: Bitmap? = null
) {

}