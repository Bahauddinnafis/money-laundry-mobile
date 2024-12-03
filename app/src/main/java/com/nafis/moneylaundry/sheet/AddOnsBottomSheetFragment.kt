package com.nafis.moneylaundry.sheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nafis.moneylaundry.databinding.AddonsBottomSheetBinding

class AddOnsBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: AddonsBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = AddonsBottomSheetBinding.inflate(inflater, container, false)

        val edtNewAddons = binding.edtNewAddons
        val edtHargaAddons = binding.edtHargaAddons
        val btnSimpan = binding.btnSimpan
        val btnBatalkan = binding.btnBatalkan
        val btnSelesai = binding.btnSelesai

        btnSimpan.setOnClickListener {
            val newAddons = edtNewAddons.text.toString()
            val hargaAddons = edtHargaAddons.text.toString()

            if (newAddons.isNotEmpty() && hargaAddons.isNotEmpty()) {
                Toast.makeText(context, "Data disimpan: $newAddons - $hargaAddons", Toast.LENGTH_SHORT).show()
                dismiss()
            } else {
                Toast.makeText(context, "Tolong isi semua data.", Toast.LENGTH_SHORT).show()
            }
        }

        btnBatalkan.setOnClickListener {
            edtNewAddons.text?.clear()
            edtHargaAddons.text?.clear()
            dismiss()
        }

        btnSelesai.setOnClickListener {
            dismiss()
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}