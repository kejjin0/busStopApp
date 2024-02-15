package ddwu.com.mobile.jsonnetworktest.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName="busStop_memo_table")
class BusStopMemoDto (
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    var busStopNum: String,
    var score: String,
    var memoContent : String
    ) : Serializable{}