package com.nafis.moneylaundry.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nafis.moneylaundry.R
import com.nafis.moneylaundry.extensions.getImageResourceByName
import com.nafis.moneylaundry.fragments.PaketLaundryFragment
import com.nafis.moneylaundry.models.packageLaundry.PaketLaundryModel
import com.nafis.moneylaundry.transaction.EditPaketActivity

class   PaketLaundryVerticalRecyclerview(
    private var listPaketLaundry: MutableList<PaketLaundryModel>,
    private val fragment: Fragment
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_PREMIUM = 0
        private const val TYPE_PACKAGE = 1
    }

    private var listener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(paketLaundry: PaketLaundryModel)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newData: List<PaketLaundryModel>) {
        listPaketLaundry.clear()
        listPaketLaundry.addAll(newData)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return listPaketLaundry.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == listPaketLaundry.size) {
            TYPE_PREMIUM
        } else {
            TYPE_PACKAGE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_PREMIUM -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.premium_row, parent, false)
                PremiumViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.paket_vertical_row, parent, false)
                PackageViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position == listPaketLaundry.size) {
            val premiumHolder = holder as PremiumViewHolder
            premiumHolder.itemView.setOnClickListener {
                listener?.onItemClick(PaketLaundryModel(name = "Subscribe", price_per_kg = 0, description = "", logo = ""))
            }
        } else {
            val currentPaketLaundry = listPaketLaundry[position]

            val btnEditPackage: Button? = holder.itemView.findViewById(R.id.btn_edit_package)
            btnEditPackage?.setOnClickListener {
                val intent = Intent(holder.itemView.context, EditPaketActivity::class.java)
                intent.putExtra("paketLaundry", currentPaketLaundry)
                Log.e("PaketLaundryAdapter", "PaketLaundryModel: $currentPaketLaundry")
                intent.putExtra("packageLaundryId", currentPaketLaundry.package_laundry_id)
                Log.e("PaketLaundryAdapter", "Package ID: ${currentPaketLaundry.package_laundry_id}")
                fragment.startActivityForResult(intent, PaketLaundryFragment.REQUEST_CODE_EDIT)
                Log.e("PaketLaundryAdapter", "Starting EditPaketActivity")
            }

            val btnDeletePackage: Button? = holder.itemView.findViewById(R.id.btn_delete_package)
            btnDeletePackage?.setOnClickListener {
                (fragment as? PaketLaundryFragment)?.deletePaketLaundry(currentPaketLaundry.package_laundry_id)
            }

            // Set data to views
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
                .into(imageView)

            holder.itemView.setOnClickListener {
                listener?.onItemClick(currentPaketLaundry)
            }
        }
    }

    class PremiumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    class PackageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
