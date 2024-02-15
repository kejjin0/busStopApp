package ddwu.com.mobile.jsonnetworktest

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import ddwu.com.mobile.jsonnetworktest.data.BusStop
import ddwu.com.mobile.jsonnetworktest.data.BusStopRoot
import ddwu.com.mobile.jsonnetworktest.databinding.ActivityMainBinding
import ddwu.com.mobile.jsonnetworktest.databinding.BusesBinding
import ddwu.com.mobile.jsonnetworktest.network.BusStopAPIService
import ddwu.com.mobile.jsonnetworktest.ui.BusStopsAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale

class MainActivity : AppCompatActivity() {
    // 20210770 컴퓨터학과 김은진
    // 모바일응용 2분반
    private val TAG = "MainActivity"

    lateinit var mainBinding : ActivityMainBinding
    lateinit var adapter : BusStopsAdapter
    lateinit var apiCall : Call<BusStopRoot>

    private lateinit var fusedLocationClient : FusedLocationProviderClient
    private lateinit var geocoder : Geocoder
    private lateinit var currentLoc : Location

    lateinit var targetLoc : LatLng
    private lateinit var googleMap : GoogleMap
    var centerMarker : Marker? = null
    var busStopMarker : Marker? = null
    var busStops: List<BusStop>? = null

    val markerList = mutableListOf<Marker?>()

    var latitude:Double = 37.606320
    var longitude:Double = 127.041808
    // LatLng(37.606320, 127.041808)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        adapter = BusStopsAdapter()
        mainBinding.busStops.adapter = adapter
        mainBinding.busStops.layoutManager = LinearLayoutManager(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this, Locale.getDefault())


        mainBinding.btnMemo.setOnClickListener{
            val intent = Intent (this@MainActivity, BusStopMemoActivity::class.java )
            startActivity(intent)
        }

        mainBinding.btnSearch.setOnClickListener{
            val busStopId :String? =  mainBinding.etBusStopID.text.toString()

            if(busStopId?.length==0){
                Toast.makeText(this, "버스정류장 ID를 입력하세요.", Toast.LENGTH_SHORT).show()
            }else{
                val intent = Intent(this@MainActivity, BusArriveActivity::class.java)
                intent.putExtra("stationId",busStopId)
                startActivity(intent)
            }
        }

        mainBinding.btnAdress.setOnClickListener{
            super.onPause()
            fusedLocationClient.removeLocationUpdates(locCallback)

            val etLocation = mainBinding.etAddress.text.toString()

            if(etLocation?.length==0){
                Toast.makeText(this, "장소를 입력하세요.", Toast.LENGTH_SHORT).show()
            }else{
                geocoder.getFromLocationName("${etLocation}",5){ addresses ->
                    CoroutineScope(Dispatchers.Main).launch {
                        latitude=addresses.get(0).latitude
                        longitude=addresses.get(0).longitude

                        if(centerMarker!=null){
                            centerMarker?.remove()
                        }

                        val apiCallback = object: Callback<BusStopRoot> {

                            override fun onResponse(call: Call<BusStopRoot>, response: Response<BusStopRoot>) {
                                if (response.isSuccessful) {
                                    val root : BusStopRoot? = response.body()
                                    adapter.busStops=root?.msgBody?.busStopList
                                    busStops = root?.msgBody?.busStopList

                                    Log.d(TAG, "${root?.msgHeader?.headerMsg}")
                                    if( root?.msgBody?.busStopList == null){
                                        Log.d(TAG, "null")
                                    }
                                    adapter.notifyDataSetChanged()

                                    markerList.forEach{
                                        it?.remove()
                                    }
                                    markerList.clear()

                                    busStops?.forEach {
                                        addBusMarker(LatLng(it.gpsY, it.gpsX), it.stationNm.toString(), it.arsId.toString())
                                    }

                                } else {
                                    Log.d(TAG, "Unsuccessful Response")
                                }
                            }

                            override fun onFailure(call: Call<BusStopRoot>, t: Throwable) {
                                Log.d(TAG, "OpenAPI Call Failure ${t.message}")
                            }
                        }

                        val retrofit = Retrofit.Builder()
                            .baseUrl(resources.getString(R.string.bus_url))
                            .addConverterFactory( GsonConverterFactory.create() )
                            .build()

                        // LatLng(37.606320, 127.041808)
                        val service = retrofit.create(BusStopAPIService::class.java)
                        apiCall = service.getBusStop(
                            resources.getString(R.string.busStop_key),
                            longitude,
                            latitude,
                            200,
                            "json" )

                        apiCall.enqueue(apiCallback)

                        addMarker(LatLng(latitude, longitude))
                    }
                }
                //2번 눌러야 함.
                targetLoc = LatLng(latitude,longitude)
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(targetLoc, 17F))
            }
        }

        mainBinding.btnLocation.setOnClickListener {
            checkPermissions ()
            startLocUpdates()
        }

        mainBinding.pauseButton.setOnClickListener{
            fusedLocationClient.removeLocationUpdates(locCallback)
        }

        val mapFragment: SupportMapFragment
                = supportFragmentManager.findFragmentById(R.id.map)
                as SupportMapFragment

        mapFragment.getMapAsync (mapReadyCallback)

        val onClickListener = object : BusStopsAdapter.OnItemClickListener{
            override fun onItemClick(view: View, position:Int){
                val intent = Intent(this@MainActivity, BusArriveActivity::class.java)
                intent.putExtra("stationId",busStops?.get(position)?.arsId)
                startActivity(intent)
            }
        }
        adapter.setOnItemClickListener(onClickListener)
    }

    //googlemap
    /*GoogleMap 로딩이 완료될 경우 실행하는 Callback*/
    val mapReadyCallback = object: OnMapReadyCallback {
        override fun onMapReady(map: GoogleMap) {
            googleMap = map
            Log.d(TAG, "GoogleMap is ready")

            googleMap.setOnMarkerClickListener { marker ->
                Toast.makeText(this@MainActivity, marker.tag.toString(), Toast.LENGTH_LONG).show()
                false
            }

            googleMap.setOnInfoWindowClickListener { marker->
                Toast.makeText(this@MainActivity, marker.title, Toast.LENGTH_LONG).show()
            }

            googleMap.setOnMapClickListener {
                Toast.makeText(this@MainActivity, it.toString(), Toast.LENGTH_SHORT).show()
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
    fun addBusMarker(targetLoc: LatLng, name:String, id:String) {
        val markerOptions : MarkerOptions = MarkerOptions()
        markerOptions.position(targetLoc)
            .title("${name}")
            .snippet("(${id})")
            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.bus_stop3))

        busStopMarker = googleMap.addMarker(markerOptions)
        busStopMarker?.showInfoWindow()
        markerList.add(busStopMarker)
        busStopMarker?.tag="${name} (${id})"
    }

    //lts
    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locCallback)
    }

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

    /*위치 정보 수신 시 수행할 동작을 정의하는 Callback*/
    val locCallback : LocationCallback = object : LocationCallback() {
        @SuppressLint("NewApi")
        override fun onLocationResult(locResult: LocationResult) {
            currentLoc = locResult.locations.get(0)
            geocoder.getFromLocation(currentLoc.latitude, currentLoc.longitude, 5) { addresses ->
                CoroutineScope(Dispatchers.Main).launch {
                    Log.d(TAG, "위도: ${currentLoc.latitude}, 경도: ${currentLoc.longitude}")

                    latitude = currentLoc.latitude
                    longitude = currentLoc.longitude

                    if(centerMarker!=null){
                        centerMarker?.remove()
                    }

                    //
                    val apiCallback = object: Callback<BusStopRoot> {

                        override fun onResponse(call: Call<BusStopRoot>, response: Response<BusStopRoot>) {
                            if (response.isSuccessful) {
                                val root : BusStopRoot? = response.body()
                                adapter.busStops=root?.msgBody?.busStopList
                                busStops = root?.msgBody?.busStopList

                                Log.d(TAG, "${root?.msgHeader?.headerMsg}")
                                if( root?.msgBody?.busStopList == null){
                                    Log.d(TAG, "null")
                                }
                                adapter.notifyDataSetChanged()

                                busStops?.forEach {
                                    addBusMarker(LatLng(it.gpsY, it.gpsX), it.stationNm.toString(), it.arsId.toString())
                                }

                            } else {
                                Log.d(TAG, "Unsuccessful Response")
                            }
                        }

                        override fun onFailure(call: Call<BusStopRoot>, t: Throwable) {
                            Log.d(TAG, "OpenAPI Call Failure ${t.message}")
                        }
                    }

                    val retrofit = Retrofit.Builder()
                        .baseUrl(resources.getString(R.string.bus_url))
                        .addConverterFactory( GsonConverterFactory.create() )
                        .build()

                    // LatLng(37.606320, 127.041808)
                    val service = retrofit.create(BusStopAPIService::class.java)
                    apiCall = service.getBusStop(
                        resources.getString(R.string.busStop_key),
                        longitude,
                        latitude,
                        200,
                        "json" )

                    apiCall.enqueue(apiCallback)
                    addMarker(LatLng(currentLoc.latitude, currentLoc.longitude))
                }
            }
            targetLoc = LatLng(currentLoc.latitude,currentLoc.longitude)
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