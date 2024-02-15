package ddwu.com.mobile.jsonnetworktest.network

import ddwu.com.mobile.jsonnetworktest.data.BusLocationRoot
import ddwu.com.mobile.jsonnetworktest.data.BusStopBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface BusLocationAPIService {
    @GET("api/rest/buspos/getBusPosByRtid")
    fun getBusLocation(
        @Query("serviceKey") serviceKey : String,
        @Query("busRouteId") busRouteId : String?,
        @Query("resultType") resultType : String,
    )
    : Call<BusLocationRoot>
}