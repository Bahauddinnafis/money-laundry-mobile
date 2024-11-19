package com.nafis.moneylaundry.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nafis.moneylaundry.R
import com.nafis.moneylaundry.models.DaftarTransaksiModel

class DaftarTransaksiRecyclerview(private val listDaftarTransaksi: List<DaftarTransaksiModel>): RecyclerView.Adapter<DaftarTransaksiRecyclerview.MyViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DaftarTransaksiRecyclerview.MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.daftar_transaksi_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: DaftarTransaksiRecyclerview.MyViewHolder, position: Int) {
        val currentDaftarTransaksi = listDaftarTransaksi[position]

        holder.itemView.findViewById<TextView>(R.id.tv_nameCustomer).text = currentDaftarTransaksi.name
        holder.itemView.findViewById<TextView>(R.id.tv_package).text = currentDaftarTransaksi.packageLaundry
        holder.itemView.findViewById<ImageView>(R.id.iv_icon).setImageResource(currentDaftarTransaksi.photo)
    }

    override fun getItemCount(): Int {
        return listDaftarTransaksi.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}