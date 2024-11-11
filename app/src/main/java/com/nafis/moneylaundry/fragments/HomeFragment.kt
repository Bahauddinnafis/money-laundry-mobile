package com.nafis.moneylaundry.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nafis.moneylaundry.R
import com.nafis.moneylaundry.adapter.PaketLaundryRecyclerview
import com.nafis.moneylaundry.data.PaketLaundry
import com.nafis.moneylaundry.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerview: RecyclerView
    private lateinit var adapter: PaketLaundryRecyclerview

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        binding.tvSeeAll.setOnClickListener {
            val fragment = PaketLaundryFragment()
            val fragmentManager = requireActivity().supportFragmentManager

            if (fragmentManager.findFragmentByTag(PaketLaundryFragment::class.java.simpleName) == null) {
                fragmentManager.beginTransaction()
                    .replace(R.id.frame_layout, fragment, PaketLaundryFragment::class.java.simpleName)
                    .addToBackStack(null)
                    .commit()
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView() {
        recyclerview = binding.rvPaketLaundry
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerview.layoutManager = layoutManager

        val allPaketData = PaketLaundry.allCategory
        adapter = PaketLaundryRecyclerview(allPaketData)
        recyclerview.adapter = adapter

        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.item_spacing)
        recyclerview.addItemDecoration(HorizontalSpaceItemDecoration(spacingInPixels))
    }

}