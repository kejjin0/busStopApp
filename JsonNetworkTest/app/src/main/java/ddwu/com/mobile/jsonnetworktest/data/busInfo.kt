package ddwu.com.mobile.jsonnetworktest.data

import com.google.gson.annotations.SerializedName

data class BusInfoRoot(
    @SerializedName("msgBody")
    val msgBody: BusInfoBody,
)

data class BusInfoBody(
    @SerializedName("itemList")
    val busInfoList: List<BusInfo>,
)

data class BusInfo(
    val busRouteId: String,
//    val busRouteNm: String,
    val busRouteAbrv: String,
//    val seq: String,
//    val section: String,
    val station: String,
    val arsId: String,
    val stationNm: String,
    val gpsX: String,
    val gpsY: String,
//    val posX: String,
//    val posY: String,
//    val fullSectDist: String,
    val direction: String,
    val stationNo: String,
//    val routeType: String,
    val beginTm: String,
    val lastTm: String,
//    val trnstnid: String,
//    val sectSpd: String,
//    val transYn: String,
)