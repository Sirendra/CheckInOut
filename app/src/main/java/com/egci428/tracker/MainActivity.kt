package com.egci428.tracker

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_main.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    //Variable declaration for accessing location
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener

    //Variable declaration for accessing Time
    private lateinit var currentTime:Date

    //Variable declaration for storing location
    private  var currentLatLng: LatLng=LatLng(0.0, 0.0)
    private var classroomLatLng:LatLng= LatLng(13.8009, 100.3236)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //initializing  location manager for location service
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        //initializing a listener for storing user's location
        //this gets activated later in program
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                if (location == null){
                    Toast.makeText(applicationContext, "Location Not Found", Toast.LENGTH_SHORT).show()
                }
                else{
                    //store current location for further use
                    currentLatLng = LatLng(location.latitude, location.longitude)

                }
            }
            override fun onProviderDisabled(provider: String) {
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        }
        //this function is to ask user's permission to access their location and activate location listener
        requestLocation()
        //this part of code checks current location with classroom's location and see whether the user is within 50m radius to check in
        check_in.setOnClickListener {
            txt1.setTextColor(Color.parseColor("#FF5689DF"));
            val loc1 = Location("")
            loc1.setLatitude(currentLatLng.latitude)
            loc1.setLongitude(currentLatLng.longitude)

            val loc2 = Location("")
            loc2.setLatitude(classroomLatLng.latitude)
            loc2.setLongitude(classroomLatLng.longitude)
            //calculate and store straight distance
            val distanceInMeters: Float = loc1.distanceTo(loc2)
           //checks whether user is within 50m to classroom
            if (distanceInMeters<=50.0){
                currentTime = Calendar.getInstance().getTime()
                val date: DateFormat = SimpleDateFormat("HH:mm a")
                val localTime = date.format(currentTime)
                txt1.text="You are in"
                checkIn_Tym.text=localTime
                check_in.visibility=GONE
                Check_Out.visibility= VISIBLE
                checkOut_Tym.text="Not yet"
            }
            //if not, ask user to come within 50m radius
            else{
                txt1.setTextColor(Color.parseColor("#9FD32D2D"));
                txt1.text="Please get in within 50m radius "
                checkOut_Tym.text="Not yet"
                checkIn_Tym.text="Not yet"
            }
        }

        //this part of code checks current location with classroom's location and see whether the user is within 50m radius to check out
        Check_Out.setOnClickListener {
            txt1.setTextColor(Color.parseColor("#FF5689DF"));
            val loc1 = Location("")
            loc1.setLatitude(currentLatLng.latitude)
            loc1.setLongitude(currentLatLng.longitude)

            val loc2 = Location("")
            loc2.setLatitude(classroomLatLng.latitude)
            loc2.setLongitude(classroomLatLng.longitude)
            //calculate and store straight distance
            val distanceInMeters: Float = loc1.distanceTo(loc2)
            //checks whether user is within 50m to classroom
            if (distanceInMeters<=50.0){
                currentTime = Calendar.getInstance().getTime()
                val date: DateFormat = SimpleDateFormat("HH:mm a")
                val localTime = date.format(currentTime)
                txt1.text="You are out"
                checkOut_Tym.text=localTime
                Check_Out.visibility= GONE
                check_in.visibility= VISIBLE
            }
            else{
                txt1.setTextColor(Color.parseColor("#9FD32D2D"));
                txt1.text="Please get in within 50m radius "
            }
        }
    }

    //ask permission and activate location listener
    private fun requestLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )!= PackageManager.PERMISSION_GRANTED){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 10)
            }
            return
        }
        locationManager.requestLocationUpdates("gps", 1000, 0f, locationListener)
//            }
//        }
    }

    //do nothing if permission denied
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode){
            10 -> requestLocation()
            else -> Toast.makeText(this, "Location Access Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    //pause accessing location 
    override fun onPause() {
        super.onPause()
        locationManager.removeUpdates(locationListener)
        Log.i("GPS Status", "pause")
    }
}