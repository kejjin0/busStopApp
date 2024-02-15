package ddwu.com.mobile.jsonnetworktest.network

import ddwu.com.mobile.jsonnetworktest.data.BusInfoRoot
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface BusInfoAPIService {
    @GET("api/rest/busRouteInfo/getStaionByRoute")
    fun getBusInfo(
        @Query("serviceKey") serviceKey : String,
        @Query("busRouteId") busRouteId : String?,
        @Query("resultType") resultType : String,
    )
    : Call<BusInfoRoot>
}