package ddwu.com.mobile.jsonnetworktest.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [BusStopMemoDto::class], version=1)
abstract class BusStopMemoDatabase : RoomDatabase() {
    abstract fun busStopMemoDao() : BusStopMemoDao

    companion object{
        @Volatile
        private var INSTANCE : BusStopMemoDatabase? = null

        fun getDatabase(context:Context) : BusStopMemoDatabase{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(context.applicationContext,
                    BusStopMemoDatabase::class.java, "busStop_memo_table").build()
                INSTANCE=instance
                instance
            }
        }
    }
}