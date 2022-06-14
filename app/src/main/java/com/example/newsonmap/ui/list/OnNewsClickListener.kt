package com.example.newsonmap.ui.list

import com.google.android.gms.maps.model.LatLng

interface OnNewsClickListener {
    fun onNewsClick(latLng: LatLng)
}