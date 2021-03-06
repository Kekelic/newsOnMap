package com.example.newsonmap.ui.map

import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.newsonmap.R
import com.example.newsonmap.databinding.FragmentMapsBinding
import com.example.newsonmap.model.MapMode
import com.example.newsonmap.model.NewsType
import com.example.newsonmap.ui.details.CreateNewsDialog
import com.example.newsonmap.ui.details.OnCreateNewsListener
import com.example.newsonmap.ui.details.ShowNewsDialog
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.*
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class MapsFragment : Fragment(), OnMapClickListener, OnMarkerClickListener, OnCreateNewsListener {

    private lateinit var binding: FragmentMapsBinding

    private var fusedLocationProviderClient: FusedLocationProviderClient? = null

    private var currentLocation: Location? = null
    private lateinit var map: GoogleMap

    private val callback = OnMapReadyCallback { googleMap ->

        map = googleMap

        val mapMode = getMapMode()
        setMapMode(MapMode.valueOf(mapMode))

        if(currentLocation!=null){
            val latLng = LatLng(currentLocation?.latitude!!, currentLocation?.longitude!!)
            map.animateCamera(CameraUpdateFactory.newLatLng(latLng))
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        }

        map.setOnMapClickListener(this)
        map.setOnMarkerClickListener(this)

        loadNews()
    }

    private fun getMapMode(): String {
        val preferenceManager = PreferenceManager()
        return preferenceManager.getMapMode()!!
    }

    private fun setMapMode(mapMode: MapMode) {
        when (mapMode) {
            MapMode.HYBRID -> {
                map.mapType = MAP_TYPE_HYBRID
            }
            MapMode.TERRAIN -> {
                map.mapType = MAP_TYPE_TERRAIN

            }
            else ->
                map.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        requireContext(),
                        R.raw.map_style
                    )
                )

        }
    }

    private fun loadNews() {
        val db = Firebase.firestore
        val documentReference = db.collection("news")
        documentReference.get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents) {
                        val date = document["date"].toString()
                        if (isDateInExpectedTime(date)) {
                            val latitude = document["latitude"].toString().toDouble()
                            val longitude = document["longitude"].toString().toDouble()
                            val type = document["type"].toString()
                            makeMarker(LatLng(latitude, longitude), type)
                        }

                    }
                } else {
                    Log.d(ContentValues.TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "get failed with ", exception)
            }
    }

    private fun isDateInExpectedTime(date: String): Boolean {
        val preferenceManager = PreferenceManager()
        val hours = preferenceManager.getTimeLastNews()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm")
            val currentDate = LocalDateTime.now()
            val newsDate = LocalDateTime.parse(date, formatter)
            val plusHoursNewsDate = newsDate.plusHours(hours.toLong())
            if (plusHoursNewsDate >= currentDate) {
                return true
            }

        } else {
            val formatter = SimpleDateFormat("dd.MM.yyyy. HH:mm")
            val currentDate = Date()
            val newsDate = formatter.parse(date)
            val dateDifference = (currentDate.time - newsDate.time)
            val hoursValue = 1000 * 60 * 60 * hours
            if (dateDifference <= hoursValue) {
                return true
            }
        }
        return false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapsBinding.inflate(layoutInflater)

        binding.fabSettings.setOnClickListener { openMapSettingsDialog() }

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchLocation()
    }

    private fun openMapSettingsDialog() {
        val dialog = MapSettingsDialog()
        dialog.show(requireActivity().supportFragmentManager, "map settings dialog")
    }

    private fun fetchLocation() {

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            val task = fusedLocationProviderClient?.lastLocation
            task?.addOnSuccessListener { location ->
                if (location != null) {
                    this.currentLocation = location
                }
                val mapFragment =
                    childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
                mapFragment?.getMapAsync(callback)

            }
        }
    }

    override fun onMapClick(latLng: LatLng) {
        val address = getAddress(latLng.latitude, latLng.longitude)
        val dialog = CreateNewsDialog(latLng, address, this)
        dialog.show(requireActivity().supportFragmentManager, "create dialog")
    }

    private fun getAddress(lat: Double, lon: Double): String {
        val geoCoder = Geocoder(context, Locale.getDefault())
        val addresses = geoCoder.getFromLocation(lat, lon, 1)
        return addresses[0].getAddressLine(0).toString()
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val dialog = ShowNewsDialog(marker.position)
        dialog.show(requireActivity().supportFragmentManager, "show dialog")
        return true
    }

    override fun createMarker(latLng: LatLng, type: String) {
        makeMarker(latLng, type)
        map.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
    }

    private fun makeMarker(latLng: LatLng, type: String) {
        val address = getAddress(latLng.latitude, latLng.longitude)
        val markerOptions = MarkerOptions().position(latLng).title("news here").snippet(address)

        when {
            NewsType.valueOf(type) == NewsType.EVENT -> {
                markerOptions.icon(bitmapDescriptorFromVector(R.drawable.ic_event))
            }
            NewsType.valueOf(type) == NewsType.AD -> {
                markerOptions.icon(bitmapDescriptorFromVector(R.drawable.ic_ad))
            }
            else -> {
                markerOptions.icon(bitmapDescriptorFromVector(R.drawable.ic_info))
            }
        }
        map.addMarker(markerOptions)
    }

    private fun bitmapDescriptorFromVector(vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(requireContext(), vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap =
                Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }

}