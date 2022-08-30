package com.suri.weather.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ObservableField
import androidx.recyclerview.widget.RecyclerView
import com.suri.weather.databinding.ListItemBinding
import com.suri.weather.model.Hour

class HourAdapter(private var hours: List<Hour>, var selectedHour: ObservableField<Hour>) : RecyclerView.Adapter<HourAdapter.MyViewHolder>() {

    var prev : Int=0
    inner class MyViewHolder(private val binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Hour){
            binding.item=item
            binding.selected=selectedHour.get()
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(hours[position])
        holder.itemView.rootView.setOnClickListener{
            selectedHour.set(hours[position])
            notifyItemChanged(prev)
            notifyItemChanged(position)
            prev=position
        }
    }

    override fun getItemCount(): Int {
        return hours.size
    }

    fun setData(it: List<Hour>) {
        hours=it
        notifyItemRangeInserted(0,hours.size)
    }
}