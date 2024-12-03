package com.nafis.moneylaundry.sheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nafis.moneylaundry.adapter.SelectImageAdapter
import com.nafis.moneylaundry.data.SelectImage
import com.nafis.moneylaundry.databinding.SelectImageSheetBinding

class SelectImageBottomSheet(private val onImageSelected: (String) -> Unit) : BottomSheetDialogFragment() {

    private var _binding: SelectImageSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SelectImageSheetBinding.inflate(inflater, container, false)

        setUpRecyclerView()

        return binding.root
    }

    private fun setUpRecyclerView() {
        val selectImageAdapter = SelectImageAdapter(SelectImage.allImage) { selectedImage ->
            // Mengirimkan nama gambar yang dipilih (imageName)
            onImageSelected(selectedImage.imageName)
            dismiss() // Menutup bottom sheet setelah gambar dipilih
        }
        binding.rvSelectImage.apply {
            adapter = selectImageAdapter
            layoutManager = GridLayoutManager(context, 2)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

