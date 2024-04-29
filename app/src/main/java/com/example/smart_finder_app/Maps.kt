package com.example.smart_finder_app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.location.Location
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.IOException

class Maps : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener{

    private lateinit var mMap: GoogleMap
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    private lateinit var database: FirebaseDatabase
    private lateinit var gpsRef: DatabaseReference

    companion object{
        internal const val LOCATION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.myMap) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        database = FirebaseDatabase.getInstance()
        gpsRef = database.getReference("KeychainData/House Keychain/GPS")

        val mapOptionButton = findViewById<ImageView>(R.id.menu)
        val popupMenu = PopupMenu(this, mapOptionButton)
        popupMenu.menuInflater.inflate(R.menu.map_options, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            changeMap(menuItem.itemId)
            true
        }

        mapOptionButton.setOnClickListener {
            popupMenu.show()
        }

        // Setup SearchView listener
        val searchView = findViewById<SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { searchLocation(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })
    }

    private fun searchLocation(locationName: String) {
        val geocoder = Geocoder(this)
        try {
            val addressList: List<Address> = geocoder.getFromLocationName(locationName, 1)!!
            if (addressList.isNotEmpty()) {
                val address: Address = addressList[0]
                mMap.clear() // Clear existing markers

                val lat = address.latitude
                val longt = address.longitude
                val mapUr = Uri.parse("https://maps.google.com/maps?daddr=$lat,$longt")
                val int = Intent(Intent.ACTION_VIEW, mapUr)
                startActivity(int)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun changeMap(itemId: Int) {
        when (itemId) {
            R.id.normal_map -> mMap?.mapType = GoogleMap.MAP_TYPE_NORMAL
            R.id.hybrid_map -> mMap?.mapType = GoogleMap.MAP_TYPE_HYBRID
            R.id.satellite_map -> mMap?.mapType = GoogleMap.MAP_TYPE_SATELLITE
            R.id.terrain_map -> mMap?.mapType = GoogleMap.MAP_TYPE_TERRAIN
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setOnMarkerClickListener(this)

        if (isLocationPermissionGranted()) {
            // Remove initial marker of user's location
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            mMap.isMyLocationEnabled = false

        gpsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val latitude = snapshot.child("Latitude").getValue(Double::class.java)
                val longitude = snapshot.child("Longitude").getValue(Double::class.java)

                Log.d("MapsActivity", "Latitude: $latitude, Longitude: $longitude")

                if (latitude != null && longitude != null) {
                    val gpsLocation = LatLng(latitude, longitude)
                    placeMarkerOnMap(gpsLocation)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(gpsLocation, 12f))
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        setUpMap()
        } else {
            requestLocationPermissions()
        }
    }

    private fun isLocationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this@Maps,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_REQUEST_CODE
        )
    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location: Location? ->

            if (location != null){
                Log.d(
                    "MapsActivity",
                    "Current Location: ${location.latitude}, ${location.longitude}"
                )
            }
        }
    }

    private fun placeMarkerOnMap(currentLatLong: LatLng) {
        mMap.clear()
        val markerOptions = MarkerOptions().position(currentLatLong)
        markerOptions.title("$currentLatLong")
        mMap.addMarker(markerOptions)
    }

    override fun onMarkerClick(p0: Marker) = false

    override fun onMapClick(latLng: LatLng) {
        // Clear existing markers
        mMap.clear()

        // Place a new marker at the clicked point
        placeMarkerOnMap(latLng)

        // Animate camera to the clicked point
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f))
    }
}


