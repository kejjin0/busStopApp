package ddwu.com.mobile.jsonnetworktest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ddwu.com.mobile.jsonnetworktest.data.Bus
import ddwu.com.mobile.jsonnetworktest.data.BusInfo
import ddwu.com.mobile.jsonnetworktest.data.BusRoot
import ddwu.com.mobile.jsonnetworktest.databinding.ArriveBusBinding
import ddwu.com.mobile.jsonnetworktest.network.BusArriveAPIService
import ddwu.com.mobile.jsonnetworktest.ui.BusArriveAdapter
import ddwu.com.mobile.jsonnetworktest.ui.BusStopMemoAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BusArriveActivity : AppCompatActivity()  {
    private val TAG = "BusArriveActivity"

    lateinit var adapter : BusArriveAdapter

    lateinit var busArriveBinding : ArriveBusBinding

    var buses: List<Bus>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        busArriveBinding = ArriveBusBinding.inflate(layoutInflater)
        setContentView(busArriveBinding.root)

        adapter = BusArriveAdapter()
        busArriveBinding.arriveInfo.adapter = adapter
        busArriveBinding.arriveInfo.layoutManager = LinearLayoutManager(this)

        val busStopId = intent.getStringExtra("stationId")
        Log.d(TAG,"정류소번호2 : ${busStopId}")

        val apiCallback = object: Callback<BusRoot> {

            override fun onResponse(call: Call<BusRoot>, response: Response<BusRoot>) {
                if (response.isSuccessful) {
                    val root : BusRoot? = response.body()
                    adapter.buses = root?.msgBody?.busList
                    buses = root?.msgBody?.busList

                    if( buses == null){
                        Log.d(TAG, "null")
                    }
                    adapter.notifyDataSetChanged()
                    busArriveBinding.busStopId.text=buses?.get(0)?.stNm

                } else {
                    Log.d(TAG, "Unsuccessful Response")
                }
            }

            override fun onFailure(call: Call<BusRoot>, t: Throwable) {
                Log.d(TAG, "OpenAPI Call Failure ${t.message}")
            }
        }

        val retrofit = Retrofit.Builder()
            .baseUrl(resources.getString(R.string.bus_url))
            .addConverterFactory( GsonConverterFactory.create() )
            .build()


        val service = retrofit.create(BusArriveAPIService::class.java)
        val apiCall : Call<BusRoot>
                = service.getBusArrive(
                resources.getString(R.string.busArrive_key),
            busStopId,
            "json", )

        apiCall.enqueue(apiCallback)


        busArriveBinding.btnList.setOnClickListener{
            val intent = Intent(this, OneBusStopMemoActivity::class.java)
            intent.putExtra("busStopId", busStopId)
            startActivity(intent)
        }

        busArriveBinding.logo5.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val onClickListener = object : BusArriveAdapter.OnItemClickListener{
            override fun onItemClick(view: View, position: Int) {
                val intent = Intent(this@BusArriveActivity, BusInfoActivity::class.java)
                intent.putExtra("busNum",buses?.get(position)?.busRouteAbrv)
                intent.putExtra("routeId", buses?.get(position)?.busRouteId)
                intent.putExtra("arsId", buses?.get(position)?.arsId)
                Log.d(TAG, "${buses?.get(position)?.busRouteAbrv}, ${buses?.get(position)?.busRouteId}")
                startActivity(intent)
            }
        }
        adapter.setOnItemClickListener(onClickListener)
    }

}