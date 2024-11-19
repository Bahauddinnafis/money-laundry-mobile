package com.nafis.moneylaundry.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nafis.moneylaundry.adapter.DaftarTransaksiRecyclerview
import com.nafis.moneylaundry.data.DaftarTransaksi
import com.nafis.moneylaundry.databinding.FragmentDaftarTransaksiBinding


class DaftarTransaksiFragment : Fragment() {
    private var _binding: FragmentDaftarTransaksiBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DaftarTransaksiRecyclerview

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

        setUpRecyclerView()

        binding.ivBackButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpRecyclerView() {
        recyclerView = binding.rvDaftarTransaksi
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager
//        recyclerView.setHasFixedSize(true)
        val allDaftarTransaksi = DaftarTransaksi.allPaketLaundry
        adapter = DaftarTransaksiRecyclerview(allDaftarTransaksi)
        recyclerView.adapter = adapter
    }
}