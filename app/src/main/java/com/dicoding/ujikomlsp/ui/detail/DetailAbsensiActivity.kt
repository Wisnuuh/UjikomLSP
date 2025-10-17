package com.dicoding.ujikomlsp.ui.detail

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.dicoding.ujikomlsp.GeofenceBroadcastReceiver
import com.dicoding.ujikomlsp.MainActivity
import com.dicoding.ujikomlsp.R
import com.dicoding.ujikomlsp.databinding.ActivityDetailAbsensiBinding
import com.dicoding.ujikomlsp.preferences.UserPreferences
import com.dicoding.ujikomlsp.preferences.dataStore
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class DetailAbsensiActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val viewModel by viewModels<DetailAbsensiViewModel>()
    private lateinit var userPreference: UserPreferences
    private lateinit var binding: ActivityDetailAbsensiBinding

    private val centerLat = -8.157614 // poltek
    private val centerLng = 113.722936
//    private val centerLat = -8.222420 // luar poltek
//    private val centerLng = 113.653966
    private val geofenceRadius = 50.0

    private lateinit var geofencingClient: GeofencingClient

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var dalamArea: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailAbsensiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        userPreference = UserPreferences.getInstance(applicationContext.dataStore)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val idAbsen = intent.getStringExtra(EXTRA_IDABSENSI)
        val mapel = intent.getStringExtra(EXTRA_MAPEL)
        val deadline = intent.getStringExtra(EXTRA_DEADLINE)

        binding.tvMapel.text = mapel
        val instant = Instant.parse(deadline)
        val zonedDateTime = instant.atZone(ZoneId.of("Asia/Jakarta"))
        val formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy HH:mm", Locale("id", "ID"))
        val hasil = zonedDateTime.format(formatter)
        binding.tvTanggal.text = "Tenggat $hasil"

        viewModel.isLoading.observe(this) {
            showLoading(it)
        }

        binding.btnSubmitAbsensi.setOnClickListener {
            if (dalamArea == true) {
                Toast.makeText(this@DetailAbsensiActivity, "Berhasil Absen", Toast.LENGTH_SHORT).show()
                lifecycleScope.launch {
                    userPreference.getSession().collect { user ->
                        if (idAbsen != null) {
                            viewModel.updateAbsen(idAbsen.toInt(), user.token, "Hadir")
                        }
                    }
                }
                val intent = Intent(this@DetailAbsensiActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this@DetailAbsensiActivity, "Tidak dapat absen", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val location = locationResult.lastLocation ?: return
            val distance = FloatArray(1)
            Location.distanceBetween(
                location.latitude,
                location.longitude,
                centerLat,
                centerLng,
                distance
            )

            if (distance[0] <= geofenceRadius) {
                // bisa absen
                Log.d("TEST GEOFENCING", "onLocationResult: BERADA DI WILAYAH GEOFENCING")
                dalamArea = true
            } else {
                Log.d("Geofence", "Di luar area: ${distance[0]} meter")
                // tidak bisa absen
                dalamArea = false
            }
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            interval = 5000 // setiap 5 detik
            fastestInterval = 2000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        intent.action = GeofenceBroadcastReceiver.ACTION_GEOFENCE_EVENT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }

    override fun onMapReady(gMap: GoogleMap) {
        mMap = gMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        getMyLocation()
        addGeofence()

        val polije = LatLng(centerLat, centerLng)
        mMap.addMarker(MarkerOptions().position(polije).title("JTI POLIJE"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(polije, 17f))
        mMap.addCircle(
            CircleOptions()
                .center(polije)
                .radius(geofenceRadius)
                .fillColor(0x22FF0000)
                .strokeColor(Color.RED)
                .strokeWidth(3f)
        )
    }

    private val requestBackgroundLocationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private val runningQOrLater = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    @TargetApi(Build.VERSION_CODES.Q)
    private val requestLocationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                if (runningQOrLater) {
                    requestBackgroundLocationPermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                } else {
                    getMyLocation()
                }
            }
        }
    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    @TargetApi(Build.VERSION_CODES.Q)
    private fun checkForegroundAndBackgroundLocationPermission(): Boolean {
        val foregroundLocationApproved = checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        val backgroundPermissionApproved =
            if (runningQOrLater) {
                checkPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            } else {
                true
            }
        return foregroundLocationApproved && backgroundPermissionApproved
    }

    @SuppressLint("MissingPermission")
    private fun getMyLocation() {
        if (checkForegroundAndBackgroundLocationPermission()) {
            mMap.isMyLocationEnabled = true
            startLocationUpdates()
        } else {
            requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    @SuppressLint("MissingPermission")
    private fun addGeofence() {
        geofencingClient = LocationServices.getGeofencingClient(this)
        val geofence = Geofence.Builder()
            .setRequestId("kampus")
            .setCircularRegion(
                centerLat,
                centerLng,
                geofenceRadius.toFloat()
            )
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL or Geofence.GEOFENCE_TRANSITION_ENTER)
            .setLoiteringDelay(5000)
            .build()
        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()
        geofencingClient.removeGeofences(geofencePendingIntent).run {
            addOnCompleteListener {
                geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent).run {
                    addOnSuccessListener {
                        Log.d(TAG, "addGeofence: Geofencing added")
                    }
                    addOnFailureListener {
                        Log.d(TAG, "Geofencing not added : ${it.message}")
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    companion object {
        const val EXTRA_IDABSENSI = "extra_idabsensi"
        const val EXTRA_MAPEL = "extra_mapel"
        const val EXTRA_DEADLINE = "extra_deadline"
        const val TAG = "DetailAbsensiActivity"
    }
}