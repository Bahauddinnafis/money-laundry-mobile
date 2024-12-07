package com.nafis.moneylaundry.sheet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nafis.moneylaundry.R
import com.nafis.moneylaundry.data.AddonDetail
import com.nafis.moneylaundry.transaction.OnAddonClickListener

class AddonsAdapter(
    private val addonsList: MutableList<AddonDetail>,
    private val listener: OnAddonClickListener
) : RecyclerView.Adapter<AddonsAdapter.AddonViewHolder>() {

    interface OnAddonClickListener {
        fun onAddonClick(addon: AddonDetail)
        fun onAddonLongClick(addon: AddonDetail)
    }

    inner class AddonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvAddonName: TextView = itemView.findViewById(R.id.tv_nama_addons)

        fun bind(addon: AddonDetail) {
            tvAddonName.text = addon.addonName
            itemView.setOnClickListener {
                listener.onAddonClick(addon)
            }
            itemView.setOnLongClickListener {
                listener.onAddonLongClick(addon)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddonViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.addons_row, parent, false)
        return AddonViewHolder(view)
    }

    override fun onBindViewHolder(holder: AddonViewHolder, position: Int) {
        holder.bind(addonsList[position])
    }

    override fun getItemCount(): Int = addonsList.size
}
