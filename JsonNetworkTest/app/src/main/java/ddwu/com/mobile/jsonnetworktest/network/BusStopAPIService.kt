package ddwu.com.mobile.jsonnetworktest.network


import ddwu.com.mobile.jsonnetworktest.data.BusStopRoot
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface BusStopAPIService {
    @GET("api/rest/stationinfo/getStationByPos")
    fun getBusStop(
        @Query("serviceKey") serviceKey : String,
        @Query("tmX") tmX : Double,
        @Query("tmY") tmY : Double,
        @Query("radius") radius: Int,
        @Query("resultType") resultType : String,
    )
    : Call<BusStopRoot>
}

