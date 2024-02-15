package ddwu.com.mobile.jsonnetworktest.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BusStopMemoDao {
    @Query("SELECT * FROM busStop_memo_table")
    fun getAllMemos() : Flow<List<BusStopMemoDto>>

    @Query("SELECT * FROM busStop_memo_table WHERE id = :id")
    suspend fun getMemoById(id: Long) : List<BusStopMemoDto>

    @Query("SELECT * FROM busStop_memo_table WHERE busStopNum= :busStopNum")
    fun getBusStopMemo(busStopNum: String?): Flow<List<BusStopMemoDto>>

    @Insert
    suspend fun insertBusStopMemo(memo: BusStopMemoDto)

    @Update
    suspend fun updateBusStopMemo(memo: BusStopMemoDto)

    @Delete
    suspend fun deleteMemo(memo: BusStopMemoDto)
}