package com.nafis.moneylaundry.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.nafis.moneylaundry.R
import com.nafis.moneylaundry.SharedPreferencesHelper
import com.nafis.moneylaundry.models.transactions.TransactionOrderItemFilter

class DaftarTransaksiRecyclerview(
    private val listDaftarTransaksi: List<TransactionOrderItemFilter?>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<DaftarTransaksiRecyclerview.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.daftar_transaksi_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentDaftarTransaksi = listDaftarTransaksi[position]

        holder.itemView.findViewById<TextView>(R.id.tv_nameCustomer).text = currentDaftarTransaksi?.customer?.name
        holder.itemView.findViewById<TextView>(R.id.tv_package).text = currentDaftarTransaksi?.packageLaundry?.name

        // Set the image for the package laundry
        val imageName = currentDaftarTransaksi?.packageLaundry?.logo ?: SharedPreferencesHelper(holder.itemView.context).getLogoUrl()
        val drawableId = getDrawableIdByName(holder.itemView.context, imageName ?: "")

        if (drawableId != 0) {
            Glide.with(holder.itemView.context)
                .load(drawableId)
                .placeholder(R.drawable.gambar1)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(holder.itemView.findViewById(R.id.iv_icon))
        } else {
            holder.itemView.findViewById<ImageView>(R.id.iv_icon).setImageResource(R.drawable.gambar1)
        }

        holder.itemView.setOnClickListener {
            currentDaftarTransaksi?.transactionOrderId?.let { transactionOrderId ->
                onItemClick(transactionOrderId)
            }
        }
    }

    override fun getItemCount(): Int {
        return listDaftarTransaksi.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private fun getDrawableIdByName(context: Context, imageName: String): Int {
        return context.resources.getIdentifier(imageName, "drawable", context.packageName)
    }
}