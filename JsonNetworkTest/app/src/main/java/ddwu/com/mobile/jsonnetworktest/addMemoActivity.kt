package ddwu.com.mobile.jsonnetworktest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import ddwu.com.mobile.jsonnetworktest.data.BusStopMemoDao
import ddwu.com.mobile.jsonnetworktest.data.BusStopMemoDatabase
import ddwu.com.mobile.jsonnetworktest.data.BusStopMemoDto
import ddwu.com.mobile.jsonnetworktest.databinding.ActivityAddMemoBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class addMemoActivity : AppCompatActivity() {
    val TAG = "addMemoActivity"

    val addMemoBinding by lazy{
        ActivityAddMemoBinding.inflate(layoutInflater)
    }

    val busStopMemoDB : BusStopMemoDatabase by lazy{
        BusStopMemoDatabase.getDatabase(this)
    }

    val memoDao : BusStopMemoDao by lazy{
        busStopMemoDB.busStopMemoDao()
    }

    lateinit var busStopId:String
    lateinit var score:String
    lateinit var memoContent:String
    lateinit var intentBusStop:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(addMemoBinding.root)

        addMemoBinding.logo4.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        intentBusStop = intent.getStringExtra("busStopId").toString()
        if(intentBusStop != "0"){
            addMemoBinding.etMemoBusStopID.setText(intentBusStop)
        }

        addMemoBinding.btnSearch3.setOnClickListener{
            val busStopId :String? =  addMemoBinding.etBusStopID2.text.toString()
            val intent = Intent(this, BusArriveActivity::class.java)
            intent.putExtra("stationId",busStopId)
            startActivity(intent)
        }

        addMemoBinding.btnCommit.setOnClickListener{
            busStopId = addMemoBinding.etMemoBusStopID.text.toString()
            Log.d(TAG, "${busStopId}")
            score = addMemoBinding.inputScore.rating.toString()
            Log.d(TAG, "${score}")
            memoContent = addMemoBinding.etContent.text.toString()
            Log.d(TAG, "${memoContent}")

            CoroutineScope(Dispatchers.IO).launch {
                memoDao.insertBusStopMemo(BusStopMemoDto(0,busStopId,score, memoContent))
            }

            Log.d(TAG, "!! add !!")
            finish()
        }
    }
}