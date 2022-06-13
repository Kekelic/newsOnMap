package com.example.newsonmap.ui.details

import com.google.android.gms.maps.model.LatLng

interface OnCreateNewsListener {
    fun createMarker(latLng: LatLng)
}