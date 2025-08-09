package com.example.edifyhub.teacher

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.edifyhub.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.button.MaterialButton
import java.util.*

class MapPickerActivity : AppCompatActivity(), OnMapReadyCallback {

    private var selectedLatLng: LatLng? = null
    private var selectedAddress: String? = null
    private var marker: Marker? = null
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {
        private const val LOCATION_PERMISSION_REQUEST = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_picker)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        findViewById<MaterialButton>(R.id.btnSelectLocation).setOnClickListener {
            if (selectedLatLng != null && selectedAddress != null) {
                val data = Intent().apply {
                    putExtra("latitude", selectedLatLng!!.latitude)
                    putExtra("longitude", selectedLatLng!!.longitude)
                    putExtra("address", selectedAddress)
                }
                setResult(Activity.RESULT_OK, data)
                finish()
            } else {
                Toast.makeText(this, "Please select a location", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<MaterialButton>(R.id.btnUseCurrentLocation).setOnClickListener {
            useCurrentLocation()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val defaultLatLng = LatLng(7.8731, 80.7718) // Sri Lanka center
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLatLng, 7f))

        map.setOnMapClickListener { latLng ->
            marker?.remove()
            marker = map.addMarker(MarkerOptions().position(latLng).title("Selected Location"))
            selectedLatLng = latLng
            selectedAddress = getAddressFromLatLng(latLng)
            marker?.snippet = selectedAddress
            marker?.showInfoWindow()
        }
    }

    private fun useCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST)
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val latLng = LatLng(location.latitude, location.longitude)
                marker?.remove()
                marker = map.addMarker(MarkerOptions().position(latLng).title("Current Location"))
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                selectedLatLng = latLng
                selectedAddress = getAddressFromLatLng(latLng)
                marker?.snippet = selectedAddress
                marker?.showInfoWindow()
            } else {
                Toast.makeText(this, "Unable to get current location", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to get location: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            useCurrentLocation()
        } else {
            Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getAddressFromLatLng(latLng: LatLng): String? {
        return try {
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                addresses[0].getAddressLine(0)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}