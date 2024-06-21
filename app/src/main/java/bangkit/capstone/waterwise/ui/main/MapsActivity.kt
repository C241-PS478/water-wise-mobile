package bangkit.capstone.waterwise.ui.main

import android.Manifest
import android.content.pm.PackageManager
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.text.TextUtils.replace
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import bangkit.capstone.waterwise.R
import bangkit.capstone.waterwise.databinding.ActivityMapsBinding
import bangkit.capstone.waterwise.ui.main.ListPostItem
import bangkit.capstone.waterwise.ui.main.MapsViewModel
import bangkit.capstone.waterwise.utils.ViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val viewModel by viewModels<MapsViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        mMap.setOnMarkerClickListener(this)

        getMyLocation()

        viewModel.getPostLocation().observe(this) { location ->

            when (location) {
                is Result.Loading -> {
                    showToast(getString(R.string.loading_alert))
                }
                is Result.Success -> {
                    addMarker(location.data.listStory)
                }
                is Result.Error -> {
                    showToast(getString(R.string.error_alert))
                }
            }
        }
    }

    private fun addMarker(location: List<ListPostItem>) {
        location.forEach { data ->
            val latLng = LatLng(data.lat!!, data.lon!!)
            val marker = mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(data.authorName)
                    .snippet(data.description)
            )
            marker?.tag = data
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val data = marker.tag as ListPostItem
        val detailFragment = DetailFragmentMaps().apply {
            setListPost(listOf(data))
        }

        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace<DetailFragmentMaps>(R.id.fragment_container_view)
            addToBackStack(null)
        }

        return true
    }
}