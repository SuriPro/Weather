package com.suri.weather.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.suri.weather.R
import com.suri.weather.adapter.HourAdapter
import com.suri.weather.databinding.ActivityHomeBinding
import com.suri.weather.view_model.HomeViewModel
import com.suri.weather.view_model.MyViewModelFactory
import java.util.*


class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationManager: LocationManager

    lateinit var viewModel: HomeViewModel
    private var dialog: BottomSheetDialog? = null


    private fun checkPermission(): Boolean = ActivityCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager

        getLocation()

        viewModel = ViewModelProvider(
            this,
            MyViewModelFactory(this, "HOME_VM")
        )[HomeViewModel::class.java]

        viewModel.getError().observe(this) {
            Toast.makeText(applicationContext, it, Toast.LENGTH_LONG).show()
        }

        val adapter = HourAdapter(emptyList(), viewModel.selectedHour)
        binding.recycle.adapter = adapter

        viewModel.listForecast.observe(this) {
            if (it != null) {
                adapter.setData(it)
            }
        }

        binding.viewModel = viewModel


    }

    private fun getLocation() {
        if (checkPermission()) {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                fusedLocationProviderClient.getCurrentLocation(
                    LocationRequest.PRIORITY_HIGH_ACCURACY,
                    object : CancellationToken() {
                        override fun onCanceledRequested(p0: OnTokenCanceledListener) =
                            CancellationTokenSource().token

                        override fun isCancellationRequested() = false
                    }
                ).addOnSuccessListener {

                    if (it != null) {
                        val geocoder = Geocoder(this, Locale.getDefault())
                        val list: List<Address> =
                            geocoder.getFromLocation(it.latitude, it.longitude, 1)

                        binding.apply {
                            txtState.text = list[0].adminArea
                            txtLocation.text = list[0].locality
                        }
                        viewModel.getWeather(it)
                    } else {
                        alertRefreshLocation()
                    }
                }
            } else {
                //request to enable location
                alertLocation()
            }
        } else {
            //show bottom sheet and get permission
            showBottomSheet()
        }
    }

    private fun showBottomSheet() {

        dialog = BottomSheetDialog(this)
        dialog?.setContentView(R.layout.bottom_sheet)
        dialog?.setCancelable(false)

        val btn = dialog?.findViewById<MaterialButton>(R.id.btn_location)

        btn?.setOnClickListener {
            getLocationPermission()
        }
        dialog?.show()
    }


    private fun getLocationPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            //show alert for permission
            alertPermission()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                101
            )
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101 && grantResults.size == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            //go to home page
            dialog?.dismiss()
            getLocation()
        } else {
            //permission denied
            Toast.makeText(applicationContext, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }


    //alerts
    private val permissionResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        {
            if (checkPermission()) {
                dialog?.dismiss()
                getLocation()
            }
        }


    private fun alertPermission() {

        val builder = AlertDialog.Builder(this)

        with(builder)
        {
            setTitle("Permission Denied!")
            setMessage("Location permission need to get current weather of your location")
            setPositiveButton("OK") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.fromParts("package", packageName, null)
                permissionResult.launch(intent)
            }
            show()
        }
    }

    private val gpsResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        {
            dialog?.dismiss()
            getLocation()
        }

    private fun alertLocation() {

        if (dialog == null) {
            dialog = BottomSheetDialog(this)
            dialog?.setContentView(R.layout.bottom_sheet)
            dialog?.setCancelable(false)
        }

        val txt = dialog?.findViewById<TextView>(R.id.txt)
        val btn = dialog?.findViewById<MaterialButton>(R.id.btn_location)
        txt?.text = "Device location is turned off, Turn on the device location"
        btn?.text = "Enable"

        btn?.setOnClickListener {
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            gpsResult.launch(intent)
        }
        dialog?.show()

    }

    private fun alertRefreshLocation() {

        val builder = AlertDialog.Builder(this)

        with(builder)
        {
            setTitle("Failed to get location")
            setMessage("some of the reason we did\'t get your current location, wait a sometime and retry")
            setPositiveButton("Retry") { _, _ ->
                getLocation()
            }
            setCancelable(false)
            show()
        }

    }


}