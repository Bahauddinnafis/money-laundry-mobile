package com.nafis.moneylaundry.sheet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nafis.moneylaundry.data.AddonDetail
import com.nafis.moneylaundry.databinding.AddonsBottomSheetBinding
import com.nafis.moneylaundry.transaction.OnAddonsAddedListener

class AddOnsBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: AddonsBottomSheetBinding? = null
    private val binding get() = _binding!!
    private var listener: OnAddonsAddedListener? = null
    private var existingAddon: AddonDetail? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnAddonsAddedListener) {
            listener = context
        } else {
            throw ClassCastException("$context must implement OnAddonsAddedListener")
        }
    }

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        existingAddon = arguments?.getParcelable("existingAddon")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = AddonsBottomSheetBinding.inflate(inflater, container, false)

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetContainer)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        val edtNewAddons = binding.edtNamaAddons
        val edtHargaAddons = binding.edtHargaAddons
        val edtJumlahAddons = binding.edtJumlahAddons
        val btnSimpan = binding.btnSimpan
        val btnBatalkan = binding.btnBatalkan

        existingAddon?.let {
            edtNewAddons.setText(it.addonName)
            edtHargaAddons.setText(it.addonPrice)
            edtJumlahAddons.setText(it.addonQuantity)
        }

        btnSimpan.setOnClickListener {
            val newAddons = edtNewAddons.text.toString()
            val hargaAddons = edtHargaAddons.text.toString()
            val jumlahAddons = edtJumlahAddons.text.toString()

            if (newAddons.isNotEmpty() && hargaAddons.isNotEmpty() && jumlahAddons.isNotEmpty()) {
                val totalHarga = hargaAddons.toDouble() * jumlahAddons.toDouble()

                if (existingAddon != null) {
                    listener?.onAddonUpdated(existingAddon!!, newAddons, hargaAddons, jumlahAddons, totalHarga.toString())
                } else {
                    listener?.onAddonsAdded(newAddons, hargaAddons, jumlahAddons, totalHarga.toString())
                }
                dismiss()
            } else {
                Toast.makeText(context, "Tolong isi semua data.", Toast.LENGTH_SHORT).show()
            }
        }

        btnBatalkan.setOnClickListener {
            if (existingAddon != null) {
                edtNewAddons.setText(existingAddon!!.addonName)
                edtHargaAddons.setText(existingAddon!!.addonPrice)
                edtJumlahAddons.setText(existingAddon!!.addonQuantity)
            } else {
                edtNewAddons.text?.clear()
                edtHargaAddons.text?.clear()
                edtJumlahAddons.text?.clear()
            }

            Toast.makeText(context, "Pengeditan dibatalkan.", Toast.LENGTH_SHORT).show()
            dismiss()
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}