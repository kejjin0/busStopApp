package ddwu.com.mobile.jsonnetworktest

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ddwu.com.mobile.jsonnetworktest.data.BusStopMemoDao
import ddwu.com.mobile.jsonnetworktest.data.BusStopMemoDatabase
import ddwu.com.mobile.jsonnetworktest.databinding.ActivityAddMemoBinding
import ddwu.com.mobile.jsonnetworktest.databinding.ActivityBusStopMemoBinding
import ddwu.com.mobile.jsonnetworktest.ui.BusStopMemoAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BusStopMemoActivity : AppCompatActivity()  {
    val busStopMemoBinding by lazy{
        ActivityBusStopMemoBinding.inflate(layoutInflater)
    }

    val busStopMemoDB : BusStopMemoDatabase by lazy{
        BusStopMemoDatabase.getDatabase(this)
    }

    val memoDao : BusStopMemoDao by lazy{
        busStopMemoDB.busStopMemoDao()
    }

    val adapter : BusStopMemoAdapter by lazy{
        BusStopMemoAdapter()
    }

    var busStopId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(busStopMemoBinding.root)

        busStopMemoBinding.memos.adapter = adapter
        busStopMemoBinding.memos.layoutManager = LinearLayoutManager(this)

        busStopId = intent.getStringExtra("busStopId")
        if(busStopId == null){
            showAllMemo()
        }else{
            showBusStopMemo()
        }

        busStopMemoBinding.logo.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        busStopMemoBinding.btnWrite.setOnClickListener{
            val intent = Intent(this, addMemoActivity::class.java)
            intent.putExtra("busStopId","0")
            startActivity(intent)
        }

        val onClickListener = object : BusStopMemoAdapter.OnItemClickListener{
            override fun onItemClick(view: View, position: Int) {
                val intent = Intent(this@BusStopMemoActivity, updateMemoActivity::class.java)
                intent.putExtra("memoDto", adapter.memoList?.get(position))
                startActivity(intent)
            }
        }
        adapter.setOnItemClickListener(onClickListener)

        busStopMemoBinding.btnSearch2.setOnClickListener{
            val etBusStopId = busStopMemoBinding.etBusStopId.text.toString()

            if(etBusStopId?.length==0){
                Toast.makeText(this, "검색어를 입력하세요.", Toast.LENGTH_SHORT).show()
            }else{
                val intent = Intent(this, BusStopMemoActivity::class.java)
                intent.putExtra("busStopId",etBusStopId)
                startActivity(intent)
            }
        }
    }

    fun showAllMemo() {
        CoroutineScope(Dispatchers.Main).launch {
            memoDao.getAllMemos().collect { memos ->
                adapter.memoList = memos
                adapter.notifyDataSetChanged()
            }
        }
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