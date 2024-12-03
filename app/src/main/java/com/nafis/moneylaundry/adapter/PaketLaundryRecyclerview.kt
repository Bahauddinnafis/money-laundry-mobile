package com.nafis.moneylaundry.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nafis.moneylaundry.R
import com.nafis.moneylaundry.extensions.getImageResourceByName
import com.nafis.moneylaundry.models.PaketLaundryModel

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

        // Bind data to the views
        holder.itemView.findViewById<TextView>(R.id.tv_paketLaundry).text = currentPaketLaundry.name

        // Memuat gambar menggunakan Glide dengan pemetaan logo ke resource drawable
        val imageView = holder.itemView.findViewById<ImageView>(R.id.iv_paketLaundry) // ID sesuai layout XML
        val context = holder.itemView.context

        // Pemetaan logo ke resource drawable
        val imageResource = getImageResourceByName(context, currentPaketLaundry.logo)
        if (imageResource == 0) {
            Log.e("PaketLaundryAdapter", "Resource not found for logo: ${currentPaketLaundry.logo}")
        }

        Glide.with(context)
            .load(imageResource) // Memuat resource drawable
            .error(R.drawable.gambar1) // Gambar placeholder jika resource tidak ditemukan
            .into(imageView)

        // Set listener untuk item
        holder.itemView.setOnClickListener {
            listener?.onItemClick(currentPaketLaundry)
        }
    }


    override fun getItemCount(): Int = listPaketLaundry.size

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
