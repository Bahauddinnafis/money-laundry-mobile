package com.nafis.moneylaundry.adapter

import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nafis.moneylaundry.R
import com.nafis.moneylaundry.models.PaketLaundryModel

class PaketLaundryRecyclerview(private val listPaketLaundry: List<PaketLaundryModel>): RecyclerView.Adapter<PaketLaundryRecyclerview.MyViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(paketLaundry: PaketLaundryModel)
    }

    private var listener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.paket_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: PaketLaundryRecyclerview.MyViewHolder, position: Int) {
        val currentPaketLaundry = listPaketLaundry[position]

        holder.itemView.findViewById<TextView>(R.id.tv_paketLaundry).text = currentPaketLaundry.name
        holder.itemView.findViewById<ImageView>(R.id.iv_paketLaundry).setImageResource(currentPaketLaundry.photo)

        holder.itemView.setOnClickListener {
            listener?.onItemClick(currentPaketLaundry)
        }
    }

    override fun getItemCount(): Int = listPaketLaundry.size

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}