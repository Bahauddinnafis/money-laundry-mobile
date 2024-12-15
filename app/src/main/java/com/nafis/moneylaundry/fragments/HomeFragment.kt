package com.nafis.moneylaundry.fragments

import android.annotation.SuppressLint
import android.app.Activity
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
import com.nafis.moneylaundry.models.packageLaundry.PaketLaundryModel
import com.nafis.moneylaundry.models.packageLaundry.ResponseGetPackage
import com.nafis.moneylaundry.models.transactions.ResponseDashboard
import com.nafis.moneylaundry.repository.LaundryRepository
import com.nafis.moneylaundry.transaction.NewTransactionActivity
import com.nafis.moneylaundry.transaction.PremiumActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


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

        updateDate()

        binding.circleImageView.setOnClickListener {
            val intent = Intent(requireContext(), ProfileActivity::class.java)
            startActivity(intent)
        }

        binding.viewFlipper.setInAnimation(requireContext(), android.R.anim.fade_in)
        binding.viewFlipper.setOutAnimation(requireContext(), android.R.anim.fade_out)
        binding.viewFlipper.setOnClickListener {
            val intent = Intent(requireContext(), PremiumActivity::class.java)
            startActivity(intent)
        }

        binding.viewFlipper.startFlipping()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            fetchAndUpdateData()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateDate() {
        val calendar = Calendar.getInstance()

        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = SimpleDateFormat("MMMM", Locale("id", "ID")).format(calendar.time)
        val year = calendar.get(Calendar.YEAR)

        binding.tvDate.text = day.toString()
        binding.tvMonth.text = month
        binding.tvYear.text = year.toString()
    }

    private fun setupRecyclerView() {
        recyclerview = binding.rvPaketLaundry
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerview.layoutManager = layoutManager

        adapter = PaketLaundryRecyclerview(mutableListOf())
        adapter.setOnItemClickListener(object : PaketLaundryRecyclerview.OnItemClickListener {
            override fun onItemClick(paketLaundry: PaketLaundryModel) {
                val sharedPreferencesHelper = SharedPreferencesHelper(requireContext())
                sharedPreferencesHelper.savePricePerKg(paketLaundry.price_per_kg)

                Log.e("PaketLaundryFragment", "Paket selected: $paketLaundry")
                val intent = Intent(requireContext(), NewTransactionActivity::class.java)
                intent.putExtra("paketLaundry", paketLaundry)
                startActivity(intent)
            }
        })

        recyclerview.adapter = adapter

        fetchAndUpdateData()
    }

    fun formatToRupiah(amount: String?): String {
        return try {
            val parsedAmount = amount?.toIntOrNull() ?: 0
            val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
            format.format(parsedAmount).replace("Rp", "Rp ").replace(",00", "")
        } catch (_: NumberFormatException) {
            "Rp. 0"
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fetchAndUpdateData() {
        Log.d("HomeFragment", "fetchAndUpdateData() called")

        if (_binding == null) {
            Log.e("HomeFragment", "Binding sudah null, tidak dapat melanjutkan.")
            return
        }

        if (userId == 0) {
            Log.e("HomeFragment", "User ID tidak ditemukan.")
            Toast.makeText(requireContext(), "User ID tidak ditemukan.", Toast.LENGTH_SHORT).show()
            return
        }

        val token = SharedPreferencesHelper(requireContext()).getToken()
        if (token.isNullOrEmpty()) {
            Log.e("HomeFragment", "Token tidak valid")
            Toast.makeText(requireContext(), "Token tidak valid", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("HomeFragment", "Menggunakan token: $token untuk user ID: $userId")

        ApiClient.instance.getDashboardData("Bearer $token", userId).enqueue(object : Callback<ResponseDashboard> {
            override fun onResponse(call: Call<ResponseDashboard>, response: Response<ResponseDashboard>) {
                Log.d("HomeFragment", "Dashboard API Response code: ${response.code()}")
                if (response.isSuccessful) {
                    val dashboardData = response.body()?.data
                    Log.d("HomeFragment", "Dashboard Data: $dashboardData")
                    if (dashboardData != null) {
                        binding.tvSaldo.text = formatToRupiah(dashboardData.totalTransactionPaid)
                        binding.tvOrder.text = dashboardData.numberOfTransactions?.toString() ?: "0"
                    }
                } else {
                    Log.e("HomeFragment", "Error fetching dashboard data: ${response.errorBody()?.string()}")
                    Toast.makeText(requireContext(), "Gagal memuat data dashboard", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseDashboard>, t: Throwable) {
                Log.e("HomeFragment", "Error fetching dashboard data: ${t.message}")
                Toast.makeText(requireContext(), "Error fetching dashboard data: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

        ApiClient.instance.getPackageLaundry("Bearer $token", userId).enqueue(object : Callback<ResponseGetPackage> {
            override fun onResponse(call: Call<ResponseGetPackage>, response: Response<ResponseGetPackage>) {
                Log.d("HomeFragment", "onResponse called")
                Log.d("HomeFragment", "Response code: ${response.code()}")

                if (response.isSuccessful) {
                    Log.d("HomeFragment", "Response sukses. Mengolah data...")
                    val paketList = response.body()?.data?.map {
                        Log.d("HomeFragment", "Fetched package laundry ID: ${it?.packageLaundryId}")
                        PaketLaundryModel(
                            package_laundry_id = it?.packageLaundryId ?: 0,
                            users_id = it?.usersId ?: 0,
                            name = it?.name ?: "",
                            price_per_kg = it?.pricePerKg ?: 0,
                            description = it?.description ?: "",
                            logo = it?.logo ?: ""
                        )
                    } ?: emptyList()
                    Log.d("HomeFragment", "Data paket laundry: $paketList")
                    adapter.updateData(paketList)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("HomeFragment", "Error fetching data: $errorBody")
                    Toast.makeText(requireContext(), "Gagal memuat data. Error: $errorBody", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseGetPackage>, t: Throwable) {
                Log.e("HomeFragment", "Error fetching data: ${t.message}")
                Toast.makeText(requireContext(), "Error fetching data: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

        Log.d("HomeFragment", "Memulai fetchPaketLaundry melalui repository")
        val laundryRepository = LaundryRepository(ApiClient, requireActivity().application)
        laundryRepository.fetchPaketLaundry(userId, "Bearer $token") { result ->
            result.onSuccess { paketList ->
                Log.d("HomeFragment", "Berhasil memuat data melalui repository: $paketList")
                adapter.updateData(paketList)
            }.onFailure { exception ->
                Log.e("HomeFragment", "Error fetching data melalui repository: ${exception.message}")
                Toast.makeText(requireContext(), "Error fetching data: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

}