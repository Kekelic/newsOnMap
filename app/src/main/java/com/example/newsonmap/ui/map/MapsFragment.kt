package com.example.newsonmap.ui.map

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.newsonmap.R
import com.example.newsonmap.databinding.FragmentMapsBinding
import com.example.newsonmap.ui.details.CreateNewsDialog
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.*
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import java.util.*

class MapsFragment : Fragment(), OnMapClickListener, OnMarkerClickListener{

    private lateinit var binding: FragmentMapsBinding

    private var fusedLocationProviderClient: FusedLocationProviderClient? = null

    private var currentLocation: Location? = null
    private lateinit var map: GoogleMap

    private val callback = OnMapReadyCallback { googleMap ->

        map = googleMap

        val latLng = LatLng(currentLocation?.latitude!!, currentLocation?.longitude!!)
        val markerOption = MarkerOptions().position(latLng).title("I am here").snippet(getAddress(latLng.latitude, latLng.longitude))
        map.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        map.addMarker(markerOption)
        map.setOnMapClickListener(this)
        map.setOnMarkerClickListener(this)



//        val sydney = LatLng(-34.0, 151.0)
//        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapsBinding.inflate(layoutInflater)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        //fetchLocation()

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchLocation()

//        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
//        mapFragment?.getMapAsync(callback)
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
                if(location != null){
                    this.currentLocation = location

                    val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
                    mapFragment?.getMapAsync(callback)
                }
            }
        }


    }


    private fun getAddress(lat: Double, lon: Double): String{
        val geoCoder = Geocoder(context, Locale.getDefault())
        val addresses = geoCoder.getFromLocation(lat, lon, 1)
        return addresses[0].getAddressLine(0).toString()
    }

    override fun onMapClick(latLng: LatLng) {
        val markerOptions = MarkerOptions().position(latLng).title("news here")
            .icon(bitmapDescriptorFromVector(R.drawable.news))
            .snippet(getAddress(latLng.latitude, latLng.longitude))
        map.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        map.addMarker(markerOptions)

        var dialog = CreateNewsDialog()
        dialog.show(requireActivity().supportFragmentManager, "My dialog")
//        replaceFragment(CreateNewsFragment(), "create news")
    }

    private fun replaceFragment(fragment: Fragment, title: String){
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)

    }

    private fun bitmapDescriptorFromVector(vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(requireContext(), vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }

    override fun onMarkerClick(latLng: Marker): Boolean {
        Toast.makeText(
            context,
            "You clicked on marker",
            Toast.LENGTH_SHORT
        ).show()
        return true
    }


}