package ddwu.com.mobile.jsonnetworktest.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ddwu.com.mobile.jsonnetworktest.data.BusStopMemoDto
import ddwu.com.mobile.jsonnetworktest.databinding.ListMemoBinding

class OneBusStopMemoAdapter : RecyclerView.Adapter<OneBusStopMemoAdapter.MemoHolder>() {
    private val TAG = "OneBusStopMemoActivity"

    var memoList: List<BusStopMemoDto>? = null

    override fun getItemCount(): Int {
        return memoList?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoHolder {
        val memoBinding = ListMemoBinding.inflate( LayoutInflater.from(parent.context), parent, false)
        return MemoHolder(memoBinding, listener)
    }

    override fun onBindViewHolder(holder: OneBusStopMemoAdapter.MemoHolder, position: Int) {
        val dto = memoList?.get(position)
        holder.memoBinding.busStopId2.text = "(" + memoList?.get(position)?.busStopNum + ")"
        var star: String? = null
        when(memoList?.get(position)?.score){
            "1.0" -> star = "★✰✰✰✰"
            "2.0" -> star = "★★✰✰✰"
            "3.0" -> star = "★★★✰✰"
            "4.0" -> star = "★★★★✰"
            "5.0" -> star = "★★★★★"
        }
        holder.memoBinding.score.text=star
        holder.memoBinding.memoContent.text= memoList?.get(position)?.memoContent
    }

    class MemoHolder(val memoBinding: ListMemoBinding, listener: OneBusStopMemoAdapter.OnItemClickListener?) :RecyclerView.ViewHolder(memoBinding.root){
        init{
            memoBinding.root.setOnClickListener{
                listener?.onItemClick(it, adapterPosition)
            }
        }
    }

    var listener : OnItemClickListener? = null

    interface OnItemClickListener{
        fun onItemClick(view: View, position:Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener?){
        this.listener=listener
    }
}