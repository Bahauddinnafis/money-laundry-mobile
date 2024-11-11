package com.nafis.moneylaundry.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nafis.moneylaundry.R
import com.nafis.moneylaundry.databinding.FragmentDaftarTransaksiBinding


class DaftarTransaksiFragment : Fragment() {
    private var _binding: FragmentDaftarTransaksiBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDaftarTransaksiBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentDaftarTransaksiBinding.bind(view)

        val buttons = listOf(
            binding.btnAllTransactions,
            binding.btnCompleted,
            binding.btnInProgress,
            binding.btnCanceled
        )

        buttons.forEach { button ->
            button.setOnClickListener {
                buttons.forEach { it.setBackgroundResource(R.drawable.button_unselected_background) }

                button.setBackgroundResource(R.drawable.button_selected_background)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}