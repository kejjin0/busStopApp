package ddwu.com.mobile.jsonnetworktest

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.room.RoomDatabase
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import ddwu.com.mobile.jsonnetworktest.data.BusInfo
import ddwu.com.mobile.jsonnetworktest.data.BusInfoRoot
import ddwu.com.mobile.jsonnetworktest.data.BusLocation
import ddwu.com.mobile.jsonnetworktest.data.BusLocationRoot
import ddwu.com.mobile.jsonnetworktest.data.BusRoot
import ddwu.com.mobile.jsonnetworktest.databinding.ActivityBusInfoBinding
import ddwu.com.mobile.jsonnetworktest.network.BusInfoAPIService
import ddwu.com.mobile.jsonnetworktest.network.BusLocationAPIService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.Locale
import kotlin.math.log


class BusInfoActivity : AppCompatActivity() {
    val TAG = "BusInfoActivity"
    val busInfoBinding by lazy{
        ActivityBusInfoBinding.inflate(layoutInflater)
    }

    var infoList : List<BusInfo>? = null
    var locationList : List<BusLocation>?= null

    var centerMarker : Marker? = null
    var busStopMarker : Marker? = null
    var locationMarker : Marker? = null
    val markerList = mutableListOf<Marker?>()

    var x = 37.606320
    var y = 127.041808
    var latitude:Double = 37.606320
    var longitude:Double = 127.041808

    private lateinit var fusedLocationClient : FusedLocationProviderClient
    private lateinit var geocoder : Geocoder
    private lateinit var currentLoc : Location
    lateinit var targetLoc : LatLng
    private lateinit var googleMap : GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(busInfoBinding.root)

        val busNum = intent.getStringExtra("busNum")
        val routeId = intent.getStringExtra("routeId")
        val arsId = intent.getStringExtra("arsid")
        busInfoBinding.busNum2.setText(busNum)

        val mapFragment:SupportMapFragment
                = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(mapReadyCallback)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder= Geocoder(this, Locale.getDefault())

        busInfoBinding.logo6.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val apiCallback = object: Callback<BusInfoRoot> {
            override fun onResponse(call: Call<BusInfoRoot>, response: Response<BusInfoRoot>) {
                if(response.isSuccessful){
                    val root : BusInfoRoot? = response.body()
                    infoList = root?.msgBody?.busInfoList

                    var i = 0
                    infoList?.forEach{
                        if( it.busRouteAbrv ==  busNum){
                            Log.d(TAG,"${routeId.toString()}")
                            var x = it.gpsX.toDouble()
                            var y = it.gpsY.toDouble()

                            if(i==0){
                                targetLoc = LatLng(y,x)
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(targetLoc, 17F))
                                i = i + 1
                            }

                            addBusMarker(LatLng(y,x), it)
                        }
                    }
                }else{
                    Log.d(TAG, "Unsuccessful Response")
                }
            }

            override fun onFailure(call: Call<BusInfoRoot>, t: Throwable) {
                Log.d(TAG, "OpenAPI Call Failure ${t.message}")
            }
        }

        val retrofit = Retrofit.Builder()
            .baseUrl(resources.getString(R.string.bus_url))
            .addConverterFactory( GsonConverterFactory.create() )
            .build()

        val service = retrofit.create(BusInfoAPIService::class.java)

        val apiCall : Call<BusInfoRoot>
            = service.getBusInfo(
                resources.getString(R.string.busInfo_key),
                routeId,
                "json"
            )


        apiCall.enqueue(apiCallback)

        busInfoBinding.btnRenew.setOnClickListener{
            CoroutineScope(Dispatchers.Main).launch {
                val apiCallback3 = object :Callback<BusLocationRoot>{

                    override fun onResponse(call: Call<BusLocationRoot>,response: Response<BusLocationRoot>) {
                        if(response.isSuccessful){
                            val root3 : BusLocationRoot?= response.body()
                            locationList = root3?.msgBody?.itemList

                            //locationMarker?.remove()
                            markerList.forEach{
                                it?.remove()
                            }
                            markerList.clear()
                            Log.d(TAG,"${markerList.toString()}")

                            locationList?.forEach{
                                addBusLocationMarker(LatLng(it.gpsY.toDouble(),it.gpsX.toDouble()))
                            }
                            Log.d(TAG,"${markerList.toString()}")

                        } else {
                            Log.d(TAG, "Unsuccessful Response")
                        }
                    }

                    override fun onFailure(call: Call<BusLocationRoot>, t: Throwable) {
                        Log.d(TAG, "OpenAPI Call Failure ${t.message}")
                    }
                }

                val retrofit = Retrofit.Builder()
                    .baseUrl(resources.getString(R.string.bus_url))
                    .addConverterFactory( GsonConverterFactory.create() )
                    .build()

                val service3 = retrofit.create(BusLocationAPIService::class.java)

                val apiCall3 : Call<BusLocationRoot>
                        = service3.getBusLocation(
                    resources.getString(R.string.busLocation_key),
                    routeId,
                    "json"
                )

                apiCall3.enqueue(apiCallback3)
            }
        }

        busInfoBinding.btnGetLocation.setOnClickListener{
            checkPermissions ()
            startLocUpdates()

            addMarker(LatLng(latitude, longitude))

            //fusedLocationClient.removeLocationUpdates(locCallback)
        }

        busInfoBinding.btnRemove.setOnClickListener{
            super.onPause()
            fusedLocationClient.removeLocationUpdates(locCallback)
        }
    }

    val mapReadyCallback = object: OnMapReadyCallback {
        override fun onMapReady(map: GoogleMap) {
            googleMap = map
            Log.d(TAG, "GoogleMap is ready")

            googleMap.setOnMarkerClickListener { marker ->
                Toast.makeText(this@BusInfoActivity, marker.tag.toString(), Toast.LENGTH_LONG).show()
                false
            }

            googleMap.setOnInfoWindowClickListener { marker->
                Toast.makeText(this@BusInfoActivity, marker.title, Toast.LENGTH_LONG).show()
            }

            googleMap.setOnMapClickListener {
                Toast.makeText(this@BusInfoActivity, it.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }


    //마커 추가
    fun addMarker(targetLoc: LatLng) {  // LatLng(37.606320, 127.041808)
        val markerOptions : MarkerOptions = MarkerOptions()
        markerOptions.position(targetLoc)
            .title("내 위치")
            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.userlocation2))

        centerMarker = googleMap.addMarker(markerOptions)
        centerMarker?.showInfoWindow()
        centerMarker?.tag="내 위치"
    }

    // 버스 정류소 위치
    fun addBusMarker(targetLoc: LatLng, busInfo: BusInfo) {
        val markerOptions : MarkerOptions = MarkerOptions()
        markerOptions.position(targetLoc)
            .title("${busInfo.stationNm}")
            .snippet("(${busInfo.arsId})")
            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.bus_stop3))

        busStopMarker = googleMap.addMarker(markerOptions)
        busStopMarker?.showInfoWindow()
        busStopMarker?.tag="${busInfo.stationNm} (${busInfo.arsId})"
    }

    fun addBusLocationMarker(targetLoc: LatLng){
        val markerOptions2 : MarkerOptions = MarkerOptions()
        markerOptions2.position(targetLoc)
            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.bus_location))

        locationMarker = googleMap.addMarker(markerOptions2)
        markerList.add(locationMarker)
        locationMarker?.showInfoWindow()
    }

    //사용자 위치
    val locRequest : LocationRequest = LocationRequest.Builder(5000)
        .setMinUpdateIntervalMillis(10000)
        .setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY)
        .build()

    fun checkPermissions () {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
            && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG,"Permissions are already granted")  // textView에 출력
        } else {
            locationPermissionRequest.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }
    }

    val locationPermissionRequest
            = registerForActivityResult( ActivityResultContracts.RequestMultiplePermissions() ) {
            permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                Log.d(TAG,"FINE_LOCATION is granted")
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                Log.d(TAG, "COARSE_LOCATION is granted")
            }
            else -> {
                Log.d(TAG, "Location permissions are required")
            }
        }
    }

    val locCallback : LocationCallback = object : LocationCallback() {
        @SuppressLint("NewApi")
        override fun onLocationResult(locResult: LocationResult) {
            currentLoc = locResult.locations.get(0)
            geocoder.getFromLocation(currentLoc.latitude, currentLoc.longitude, 5) { addresses ->
                CoroutineScope(Dispatchers.Main).launch {
                    latitude = currentLoc.latitude
                    longitude = currentLoc.longitude

                    if(centerMarker!=null){
                        centerMarker?.remove()
                    }
                    addMarker(LatLng(currentLoc.latitude,currentLoc.longitude))

                    targetLoc = LatLng(currentLoc.latitude,currentLoc.longitude)
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(targetLoc, 17F))
                }
            }
            val targetLoc : LatLng = LatLng(currentLoc.latitude,currentLoc.longitude)

            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(targetLoc, 17F))
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocUpdates() {
        fusedLocationClient.requestLocationUpdates(
            locRequest,
            locCallback,
            Looper.getMainLooper()
        )
    }

}