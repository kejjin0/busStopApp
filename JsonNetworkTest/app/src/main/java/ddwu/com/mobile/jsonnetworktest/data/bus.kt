package ddwu.com.mobile.jsonnetworktest.data

import com.google.gson.annotations.SerializedName

data class BusRoot(
    @SerializedName("msgBody")
    val msgBody: BusBody,
)

data class BusBody(
    @SerializedName("itemList")
    val busList: List<Bus>,
)

data class Bus(
    val stId: String,
    val stNm: String,
    val arsId: String,

    val busRouteId: String,
    val busRouteAbrv: String,
    val sectNm: String,
    val gpsX: String,
    val gpsY: String,

//    val firstTm: String,
//    val lastTm: String,

    val term: String,
    val vehId1: String,
    val vehId2: String,

    val adirection: String,
    val arrmsg1: String,
    val arrmsg2: String,
    val arrmsgSec1: String,
    val arrmsgSec2: String,
)