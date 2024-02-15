package ddwu.com.mobile.jsonnetworktest.data

import com.google.gson.annotations.SerializedName

data class BusLocationRoot(
    @SerializedName("msgBody")
    val msgBody: MsgBody,
)

data class MsgBody(
    @SerializedName("itemList")
    val itemList: List<BusLocation>,
)

data class BusLocation(
    val sectOrd: String,
//    val fullSectDist: String,
//    val sectDist: String,
//    val rtDist: String,
//    val stopFlag: String,
//    val sectionId: String,
//    val dataTm: String,
//    val tmX: Any?,
//    val tmY: Any?,
    val gpsX: String,
    val gpsY: String,
//    val posX: String,
//    val posY: String,
    val vehId: String,
    val plainNo: String,
//    val busType: String,
//    val lastStTm: String,
//    val nextStTm: String,
//    val nextStId: String,
//    val lastStnId: String,
//    val trnstnid: String,
//    val isrunyn: String,
//    val islastyn: String,
    val isFullFlag: String,
    val congetion: String,
)