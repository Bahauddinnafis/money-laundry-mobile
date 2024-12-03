package com.nafis.moneylaundry.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nafis.moneylaundry.ProfileActivity
import com.nafis.moneylaundry.R
import com.nafis.moneylaundry.SharedPreferencesHelper
import com.nafis.moneylaundry.adapter.PaketLaundryRecyclerview
import com.nafis.moneylaundry.api.ApiClient
import com.nafis.moneylaundry.auth.LoginActivity
import com.nafis.moneylaundry.databinding.FragmentHomeBinding
import com.nafis.moneylaundry.models.PaketLaundryModel
import com.nafis.moneylaundry.models.ResponseGetPackage
import com.nafis.moneylaundry.repository.LaundryRepository
import com.nafis.moneylaundry.transaction.NewTransactionActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerview: RecyclerView
    private lateinit var adapter: PaketLaundryRecyclerview
    private var userId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val sharedPrefsHelper = SharedPreferencesHelper(requireContext())
        userId = sharedPrefsHelper.getUserId()
        val token = sharedPrefsHelper.getToken()

        if (userId == -1 || token.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Data pengguna tidak ditemukan. Silakan login ulang.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        } else {
            Log.d("HomeFragment", "User  ID: $userId")
            fetchAndUpdateData()
        }

        val sharedPreferencesHelper = SharedPreferencesHelper(requireContext())
        val storeName = sharedPreferencesHelper.getStoreName()
        val storeAddress = sharedPreferencesHelper.getStoreAddress()

        Log.d("HomeFragment", "Store Name from SharedPreferences: $storeName")
        Log.d("HomeFragment", "Store Address from SharedPreferences: $storeAddress")

        binding.tvName.text = storeName
        binding.tvAddress.text = storeAddress

        return binding.root
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

        binding.circleImageView.setOnClickListener {
            val intent = Intent(requireContext(), ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            fetchAndUpdateData()
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

        adapter = PaketLaundryRecyclerview(mutableListOf())

        adapter.setOnItemClickListener(object : PaketLaundryRecyclerview.OnItemClickListener {
            override fun onItemClick(paketLaundry: PaketLaundryModel) {
                val intent = Intent(requireContext(), NewTransactionActivity::class.java)
                intent.putExtra("paketLaundry", paketLaundry)
                startActivity(intent)
            }
        })

        recyclerview.adapter = adapter

        fetchAndUpdateData()
    }

    private fun fetchAndUpdateData() {
        if (userId == 0) {
            Toast.makeText(requireContext(), "User  ID tidak ditemukan.", Toast.LENGTH_SHORT).show()
            return
        }

        val token = SharedPreferencesHelper(requireContext()).getToken()
        if (token.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Token tidak valid", Toast.LENGTH_SHORT).show()
            return }

        ApiClient.instance.getPackageLaundry("Bearer $token", userId).enqueue(object : Callback<ResponseGetPackage> {
            override fun onResponse(call: Call<ResponseGetPackage>, response: Response<ResponseGetPackage>) {
                if (response.isSuccessful) {
                    val paketList = response.body()?.data?.map {
                        PaketLaundryModel(
                            name = it?.name ?: "",
                            price_per_kg = it?.pricePerKg ?: 0,
                            description = it?.description ?: "",
                            logo = it?.logo ?: ""
                        )
                    } ?: emptyList()
                    adapter.updateData(paketList)
                } else {
                    Log.e("HomeFragment", "Error fetching data: ${response.errorBody()?.string()}")
                    Toast.makeText(requireContext(), "Gagal memuat data.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseGetPackage>, t: Throwable) {
                Log.e("HomeFragment", "Error fetching data: ${t.message}")
                Toast.makeText(requireContext(), "Error fetching data: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

        val laundryRepository = LaundryRepository(ApiClient, requireActivity().application)
        laundryRepository.fetchPaketLaundry(userId, "Bearer $token") { result ->
            result.onSuccess { paketList ->
                adapter.updateData(paketList)
            }.onFailure { exception ->
                Log.e("HomeFragment", "Error fetching data: ${exception.message}")
                Toast.makeText(requireContext(), "Error fetching data: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}