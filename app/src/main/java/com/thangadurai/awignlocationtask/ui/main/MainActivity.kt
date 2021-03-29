package com.thangadurai.awignlocationtask.ui.main

import android.annotation.SuppressLint
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.thangadurai.awignlocationtask.R
import com.thangadurai.awignlocationtask.databinding.ActivityMainBinding
import com.thangadurai.awignlocationtask.databinding.DialogGoToSettingsBinding
import com.thangadurai.awignlocationtask.ui.base.BaseActivity
import com.thangadurai.awignlocationtask.utils.AskPermission
import com.thangadurai.awignlocationtask.utils.AskPermission.showGoToSettingsDialog
import java.io.IOException

class MainActivity : BaseActivity(), OnMapReadyCallback, View.OnClickListener {

    lateinit var binding: ActivityMainBinding
    lateinit var client: FusedLocationProviderClient

    private var mMap: GoogleMap? = null
    var getAddress: MutableList<Address> =
        ArrayList()
    var lastLocation: Location? = null

    private val distance = FloatArray(2)
    private var circle = CircleOptions()

    private val mainViewModel by viewModels<MainViewModel>()

    companion object {
        const val RADIUS = 180.0
        const val ZOOM = 17f
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.mainViewModel = mainViewModel
        binding.listener = this
        setContentView(binding.root)

        setUpMapViews()

        client = LocationServices.getFusedLocationProviderClient(this)

        getCurrentLocation()


    }

    private fun setUpMapViews() {
        (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)?.getMapAsync(this)
    }

    private fun getCurrentLocation() {
        if (AskPermission.checkVersion()) {
            if (!AskPermission.checkPermission(
                    this,
                    arrayOf(AskPermission.LOCATION_FINE)
                )
            ) {
                AskPermission.requestPermission(
                    this, arrayOf(AskPermission.LOCATION_FINE)
                )
            } else {
                client.lastLocation.addOnSuccessListener {
                    animateCamera(location = it)
                    lastLocation = it
                    circle.center(LatLng(it.latitude, it.longitude))
                        .radius(RADIUS)
                        .strokeColor(ContextCompat.getColor(this, R.color.blue))
                        .fillColor(ContextCompat.getColor(this, R.color.blue))

                    mMap?.addCircle(circle)

                }.addOnFailureListener {
                    print(it.message)
                }
            }
        }
    }

    private fun animateCamera(location: Location) {
        mMap?.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    location.latitude,
                    location.longitude
                ), ZOOM
            )
        )
    }

    override fun onMapReady(gMap: GoogleMap) {

        mMap = gMap

        mMap?.setOnCameraIdleListener {

            val geoCoder = Geocoder(this@MainActivity)

            val latLng: LatLng = mMap?.cameraPosition?.target!!
            try {
                getAddress = geoCoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            } catch (e: IOException) {
                print(e.message)
            }

            binding.imgMarker.animation = AnimationUtils.loadAnimation(
                this@MainActivity,
                R.anim.map_marker_animator
            )


            lastLocation?.let {
                Location.distanceBetween(
                    latLng.latitude, latLng.longitude, it.latitude,
                    it.longitude, distance
                )
                if (distance[0] <= circle.radius) {
                    getAddress.forEachIndexed { index, address ->
                        mainViewModel.address.set(address.getAddressLine(index))
                        changeIcon(binding.imgMarker, R.drawable.ic_map_pin)
                    }
                } else {
                    showMessage(getString(R.string.txt_your_not_allowed))
                    mainViewModel.address.set(getString(R.string.txt_your_not_allowed))
                    changeIcon(binding.imgMarker, R.drawable.ic_wrong)
                }
            }
        }


    }


    @SuppressLint("InflateParams")
    fun showInfoDialog(
        body: String,
        btnText: String
    ) {

        val builder = AlertDialog.Builder(this)
        val alertDialog = builder.create()

        val binding: DialogGoToSettingsBinding = DataBindingUtil.inflate(
            LayoutInflater.from(this),
            R.layout.dialog_go_to_settings,
            null,
            false
        )
        alertDialog.setView(binding.root)

        binding.tvGoToSettings.text = body
        binding.btnGoToSettings.text = btnText

        binding.btnGoToSettings.setOnClickListener {
            alertDialog.dismiss()
            getCurrentLocation()
        }

        alertDialog.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            AskPermission.PERMISSION_CODE -> {
                if (AskPermission.handlePermissionsResult(requestCode, permissions, grantResults)) {
                    getCurrentLocation()
                } else {
                    if (!AskPermission.shouldShowRequestPermissionRationale(
                            this,
                            arrayOf(AskPermission.LOCATION_FINE)
                        )
                    ) {
                        showGoToSettingsDialog(
                            this,
                            AskPermission.ALREADY_PERMISSION_DENY_TEXT
                        )
                    } else {
                        showInfoDialog(
                            body = getString(R.string.txt_need_location_access),
                            btnText = getString(R.string.txt_ok)
                        )
                    }
                }
            }

        }


    }

    override fun onClick(v: View) {
        when (v) {
            binding.imgMyLocation -> {
                getCurrentLocation()
            }
        }

    }


}