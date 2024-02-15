package ddwu.com.mobile.jsonnetworktest.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ddwu.com.mobile.jsonnetworktest.data.Bus
import ddwu.com.mobile.jsonnetworktest.databinding.BusesBinding

class BusArriveAdapter : RecyclerView.Adapter<BusArriveAdapter.BusHolder>()  {
    private val TAG = "BusArriveActivity"

    var buses:List<Bus>? = null

    override fun getItemCount(): Int {
        return buses?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusArriveAdapter.BusHolder {
        val itemBinding = BusesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BusArriveAdapter.BusHolder(itemBinding, listener)
    }

    override fun onBindViewHolder(holder: BusArriveAdapter.BusHolder, position: Int) {
        holder.itemBinding.busNum.text = buses?.get(position)?.busRouteAbrv
        holder.itemBinding.adirection.text = buses?.get(position)?.adirection + "행"
        holder.itemBinding.arriveInfo1.text=buses?.get(position)?.arrmsg1
        holder.itemBinding.arriveInfo2.text = buses?.get(position)?.arrmsg2
        holder.itemBinding.routeId.text=buses?.get(position)?.busRouteId
    }

    class BusHolder(val itemBinding: BusesBinding, listener: OnItemClickListener?) : RecyclerView.ViewHolder(itemBinding.root){
        init {
            itemBinding.root.setOnClickListener {
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