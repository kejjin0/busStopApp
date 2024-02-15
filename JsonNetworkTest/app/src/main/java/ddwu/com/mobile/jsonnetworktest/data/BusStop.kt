package ddwu.com.mobile.jsonnetworktest.data

import com.google.gson.annotations.SerializedName

data class BusStopRoot(
    @SerializedName("msgHeader")
    val msgHeader: MsgHeader,
    @SerializedName("msgBody")
    val msgBody: BusStopBody,
)

data class MsgHeader(
    @SerializedName("headerMsg")
    val headerMsg: String,
)

data class BusStopBody(
    @SerializedName("itemList")
    val busStopList: List<BusStop>,
)

data class BusStop(
    @SerializedName("arsId")
    val arsId: String?,   //정류소 번호
    @SerializedName("dist")
    val dist: String?,     //거리
    @SerializedName("gpsX")
    val gpsX: Double,
    @SerializedName("gpsY")
    val gpsY: Double,
    @SerializedName("stationId")
    val stationId: String?,    //정류소 고유 id
    @SerializedName("stationNm")
    val stationNm: String?,  //정류소 명
//    @SerializedName("stationTp")
//    val stationTp: Long?,    //정류소 타입
)

