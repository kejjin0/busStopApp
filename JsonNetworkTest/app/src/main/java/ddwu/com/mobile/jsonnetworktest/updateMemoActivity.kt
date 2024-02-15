package ddwu.com.mobile.jsonnetworktest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import ddwu.com.mobile.jsonnetworktest.data.BusStopMemoDao
import ddwu.com.mobile.jsonnetworktest.data.BusStopMemoDatabase
import ddwu.com.mobile.jsonnetworktest.data.BusStopMemoDto
import ddwu.com.mobile.jsonnetworktest.databinding.ActivityUpdateMamoBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class updateMemoActivity : AppCompatActivity() {
    lateinit var updateBinding: ActivityUpdateMamoBinding

    val busStopMemoDB : BusStopMemoDatabase by lazy{
        BusStopMemoDatabase.getDatabase(this)
    }

    val memoDao : BusStopMemoDao by lazy{
        busStopMemoDB.busStopMemoDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val TAG = "updateMemoActivity"
        super.onCreate(savedInstanceState)
        updateBinding = ActivityUpdateMamoBinding.inflate(layoutInflater)
        setContentView(updateBinding.root)

        var dto = intent.getSerializableExtra("memoDto") as BusStopMemoDto

        var id = dto.id
        var busStopId2 = dto.busStopNum
        var score = dto.score
        var wContent = dto.memoContent

        updateBinding.etMemoBusStopID.setText(busStopId2)
        updateBinding.etMemoBusStopID.setHint(busStopId2)
        updateBinding.inputScore.rating = score.toFloat()
        updateBinding.etContent.setText(wContent)
        updateBinding.etContent.setHint(wContent)

        updateBinding.logo4.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        updateBinding.btnCommit.setOnClickListener{
            busStopId2 = updateBinding.etMemoBusStopID.text.toString()
            score = updateBinding.inputScore.rating.toString()
            wContent = updateBinding.etContent.text.toString()

            CoroutineScope(Dispatchers.IO).launch {
                memoDao.updateBusStopMemo(BusStopMemoDto(id,busStopId2,score,wContent))
            }

            Log.d(TAG, "!! update !!")
            finish()
        }

        updateBinding.btnDelete.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                memoDao.deleteMemo(BusStopMemoDto(id,busStopId2,score,wContent))
            }
            Log.d(TAG, "!! delete !!")
            finish()
        }

    }
}