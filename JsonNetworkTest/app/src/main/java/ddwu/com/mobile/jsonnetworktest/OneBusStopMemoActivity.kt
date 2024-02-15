package ddwu.com.mobile.jsonnetworktest

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ddwu.com.mobile.jsonnetworktest.data.BusStopMemoDao
import ddwu.com.mobile.jsonnetworktest.data.BusStopMemoDatabase
import ddwu.com.mobile.jsonnetworktest.databinding.ActivityOneBusStopMemoBinding
import ddwu.com.mobile.jsonnetworktest.ui.BusStopMemoAdapter
import ddwu.com.mobile.jsonnetworktest.ui.OneBusStopMemoAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class OneBusStopMemoActivity : AppCompatActivity() {
    val oneBusStopMemoBinding by lazy{
        ActivityOneBusStopMemoBinding.inflate(layoutInflater)
    }

    val busStopMemoDB : BusStopMemoDatabase by lazy{
        BusStopMemoDatabase.getDatabase(this)
    }

    val memoDao : BusStopMemoDao by lazy{
        busStopMemoDB.busStopMemoDao()
    }

    lateinit var adapter : OneBusStopMemoAdapter

    var busStopId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(oneBusStopMemoBinding.root)

        adapter = OneBusStopMemoAdapter()
        oneBusStopMemoBinding.oneBusMomes.adapter=adapter
        oneBusStopMemoBinding.oneBusMomes.layoutManager = LinearLayoutManager(this)

        busStopId = intent.getStringExtra("busStopId")
        oneBusStopMemoBinding.busStopIdText.setText(busStopId)

        showBusStopMemo()

        oneBusStopMemoBinding.logo3.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        oneBusStopMemoBinding.btnWrite2.setOnClickListener(){
            val intent = Intent(this, addMemoActivity::class.java)
            intent.putExtra("busStopId", busStopId)
            startActivity(intent)
        }

        val onClickListener = object : OneBusStopMemoAdapter.OnItemClickListener{
            override fun onItemClick(view: View, position: Int) {
                val intent = Intent(this@OneBusStopMemoActivity, updateMemoActivity::class.java)
                intent.putExtra("memoDto", adapter.memoList?.get(position))
                startActivity(intent)
            }
        }
        adapter.setOnItemClickListener(onClickListener)
    }

    fun showBusStopMemo(){
        CoroutineScope(Dispatchers.Main).launch {
            memoDao.getBusStopMemo(busStopId).collect(){memos->
                adapter.memoList = memos
                adapter.notifyDataSetChanged()
            }
        }
    }
}