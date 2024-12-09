package com.nafis.moneylaundry.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.nafis.moneylaundry.R
import com.nafis.moneylaundry.extensions.getImageResourceByName
import com.nafis.moneylaundry.models.packageLaundry.PaketLaundryModel

class PaketLaundryRecyclerview(
    private var listPaketLaundry: MutableList<PaketLaundryModel>
) : RecyclerView.Adapter<PaketLaundryRecyclerview.MyViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(paketLaundry: PaketLaundryModel)
    }

    private var listener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newData: List<PaketLaundryModel>) {
        listPaketLaundry.clear()
        listPaketLaundry.addAll(newData)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.paket_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentPaketLaundry = listPaketLaundry[position]

        holder.itemView.findViewById<TextView>(R.id.tv_paketLaundry).text = currentPaketLaundry.name

        val imageView = holder.itemView.findViewById<ImageView>(R.id.iv_paketLaundry)
        val context = holder.itemView.context

        val imageResource = getImageResourceByName(context, currentPaketLaundry.logo)
        if (imageResource == 0) {
            Log.e("PaketLaundryAdapter", "Resource not found for logo: ${currentPaketLaundry.logo}")
        }

        Glide.with(context)
            .load(imageResource)
            .error(R.drawable.gambar1)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(imageView)

        holder.itemView.setOnClickListener {
            listener?.onItemClick(currentPaketLaundry)
        }
    }


    override fun getItemCount(): Int = listPaketLaundry.size

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
