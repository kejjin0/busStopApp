package ddwu.com.mobile.jsonnetworktest.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ddwu.com.mobile.jsonnetworktest.data.BusStop
import ddwu.com.mobile.jsonnetworktest.databinding.ListItemBinding

class BusStopsAdapter : RecyclerView.Adapter<BusStopsAdapter.BusStopHolder>() {
    private val TAG = "MainActivity"

    var busStops: List<BusStop>? = null

    override fun getItemCount(): Int {
        return busStops?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusStopHolder {
        val itemBinding = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BusStopHolder(itemBinding, listener)
    }

    override fun onBindViewHolder(holder: BusStopHolder, position: Int) {
//        holder.itemBinding.tvItem.text = busStops?.get(position).toString()
        holder.itemBinding.busStopNm.text=busStops?.get(position)?.stationNm
        holder.itemBinding.busStopNum.text= "(" + busStops?.get(position)?.arsId + ")"
        holder.itemBinding.dist.text=busStops?.get(position)?.dist + "m"
    }

    class BusStopHolder(val itemBinding: ListItemBinding, listener: OnItemClickListener?) : RecyclerView.ViewHolder(itemBinding.root){
        init{
            itemBinding.root.setOnClickListener{
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