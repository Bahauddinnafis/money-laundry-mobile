package com.nafis.moneylaundry.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nafis.moneylaundry.adapter.PaketLaundryVerticalRecyclerview
import com.nafis.moneylaundry.data.PaketLaundry
import com.nafis.moneylaundry.databinding.FragmentPaketLaundryBinding
import com.nafis.moneylaundry.models.PaketLaundryModel
import com.nafis.moneylaundry.transaction.AddPaketActivity
import com.nafis.moneylaundry.transaction.NewTransactionActivity

class PaketLaundryFragment : Fragment() {
    private var _binding: FragmentPaketLaundryBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PaketLaundryVerticalRecyclerview

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaketLaundryBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView()

        binding.cardAddPaket.setOnClickListener {
            val intent = Intent(requireContext(), AddPaketActivity::class.java)
            startActivity(intent)
        }

        binding.ivBackButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpRecyclerView() {
        recyclerView = binding.rvPaketLaundry
        val layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.layoutManager = layoutManager

        val allPaketData = PaketLaundry.allCategory
        adapter = PaketLaundryVerticalRecyclerview(allPaketData)

        // Recycler View Vertical
        adapter.setOnItemClickListener(object : PaketLaundryVerticalRecyclerview.OnItemClickListener {
            override fun onItemClick(paketLaundry: PaketLaundryModel) {
                val intent = Intent(requireContext(), NewTransactionActivity::class.java)
                intent.putExtra("paketLaundry", paketLaundry)
                startActivity(intent)
            }
        })

        recyclerView.adapter = adapter
    }
}