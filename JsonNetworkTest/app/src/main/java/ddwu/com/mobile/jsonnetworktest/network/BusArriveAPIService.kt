package ddwu.com.mobile.jsonnetworktest.network

import ddwu.com.mobile.jsonnetworktest.data.BusRoot
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface BusArriveAPIService {
    @GET("api/rest/stationinfo/getStationByUid")
    fun getBusArrive(
        @Query("serviceKey") serviceKey : String,
        @Query("arsId") arsId : String?,
        @Query("resultType") resultType : String,
    )
    : Call<BusRoot>
}