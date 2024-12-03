package com.nafis.moneylaundry.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.nafis.moneylaundry.SharedPreferencesHelper
import com.nafis.moneylaundry.adapter.PaketLaundryVerticalRecyclerview
import com.nafis.moneylaundry.api.ApiClient
import com.nafis.moneylaundry.databinding.FragmentPaketLaundryBinding
import com.nafis.moneylaundry.models.PaketLaundryModel
import com.nafis.moneylaundry.models.ResponseGetPackage
import com.nafis.moneylaundry.repository.LaundryRepository
import com.nafis.moneylaundry.transaction.AddPaketActivity
import com.nafis.moneylaundry.transaction.NewTransactionActivity
import com.nafis.moneylaundry.viewmodel.UserViewModel
import com.nafis.moneylaundry.viewmodel.UserViewModelFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PaketLaundryFragment : Fragment() {
    private var _binding: FragmentPaketLaundryBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: PaketLaundryVerticalRecyclerview
    private lateinit var userViewModel: UserViewModel
    private val listPaket = mutableListOf<PaketLaundryModel>()

    companion object {
        const val REQUEST_CODE_ADD = 100
        const val REQUEST_CODE_EDIT = 101
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaketLaundryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val laundryRepository = LaundryRepository(ApiClient, requireActivity().application)
        val factory = UserViewModelFactory(requireActivity().application, laundryRepository)

        userViewModel = ViewModelProvider(this, factory).get(UserViewModel::class.java)

        // Setup RecyclerView
        binding.rvPaketLaundry.layoutManager = GridLayoutManager(requireContext(), 2)
        adapter = PaketLaundryVerticalRecyclerview(mutableListOf(), this)
        binding.rvPaketLaundry.adapter = adapter
        adapter.setOnItemClickListener(object : PaketLaundryVerticalRecyclerview.OnItemClickListener {
            override fun onItemClick(paketLaundry: PaketLaundryModel) {
                val intent = Intent(requireContext(), NewTransactionActivity::class.java)
                intent.putExtra("paketLaundry", paketLaundry)
                startActivity(intent)
            }
        })

        userViewModel.paketLaundryList.observe(viewLifecycleOwner) { paketList ->
            val updatedList = mutableListOf<PaketLaundryModel>()
            val subscribeItem = PaketLaundryModel(name = "Subscribe", price_per_kg = 0, description = "", logo = "")

            if (paketList.isEmpty()) {
                updatedList.add(subscribeItem)
            } else {
                updatedList.addAll(paketList)
            }

            adapter.updateData(updatedList)
        }

        // Fetch data
        val userId = userViewModel.getUserId()
        if (userId != 0) {
            Log.e("PaketLaundryFragment", "User ID: $userId")
            fetchAndUpdateData()
        } else {
            Toast.makeText(requireContext(), "User  ID tidak ditemukan.", Toast.LENGTH_SHORT).show()
        }

        binding.cardAddPaket.setOnClickListener {
            val intent = Intent(requireContext(), AddPaketActivity::class.java)
            intent.putExtra("isEdit", false)
            startActivityForResult(intent, REQUEST_CODE_ADD)
        }

        binding.ivBackButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    fun deletePaketLaundry(packageLaundryId: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Paket Laundry")
            .setMessage("Apakah Anda yakin ingin menghapus paket laundry ini?")
            .setPositiveButton("Ya") { _, _ ->
                userViewModel.deletePaketLaundry(packageLaundryId)

                userViewModel.deletePaketResult.observe(viewLifecycleOwner) { result ->
                    result.onSuccess {
                        val position = listPaket.indexOfFirst { paket -> paket.package_laundry_id == packageLaundryId }
                        if (position != -1) {
                            listPaket.removeAt(position)
                            adapter.updateData(listPaket)
                            Toast.makeText(requireContext(), "Paket laundry berhasil dihapus", Toast.LENGTH_SHORT).show()
                        }
                    }.onFailure { exception ->
                        Toast.makeText(requireContext(), "Gagal menghapus paket laundry: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Tidak", null)
            .show()
    }

    private fun fetchAndUpdateData() {
        val userId = userViewModel.getUserId()
        if (userId == 0) {
            Toast.makeText(requireContext(), "User  ID tidak ditemukan.", Toast.LENGTH_SHORT).show()
            return
        }

        val token = SharedPreferencesHelper(requireContext()).getToken()
        if (token.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Token tidak valid", Toast.LENGTH_SHORT).show()
            return
        }

        ApiClient.instance.getPackageLaundry("Bearer $token", userId).enqueue(object : Callback<ResponseGetPackage> {
            override fun onResponse(call: Call<ResponseGetPackage>, response: Response<ResponseGetPackage>) {
                if (response.isSuccessful) {
                    Log.e("PaketLaundryFragment", "Response body: ${response.body()}")
                    val paketList = response.body()?.data?.map {
                        PaketLaundryModel(
                            package_laundry_id = it?.packageLaundryId ?: 0,
                            users_id = it?.usersId ?: 0,
                            name = it?.name ?: "",
                            price_per_kg = it?.pricePerKg ?: 0,
                            description = it?.description ?: "",
                            logo = it?.logo ?: ""
                        )
                    } ?: emptyList()
                    adapter.updateData(paketList)
                    Log.e("PaketLaundryFragment", "Paket list: $paketList")
                } else {
                    Log.e("PaketLaundryFragment", "Error fetching data: ${response.errorBody()?.string()}")
                    Toast.makeText(requireContext(), "Gagal memuat data.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call <ResponseGetPackage>, t: Throwable) {
                Log.e("PaketLaundryFragment", "Error fetching data: ${t.message}")
                Toast.makeText(requireContext(), "Error fetching data: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_ADD -> {
                    val newPaket: PaketLaundryModel? = data?.getParcelableExtra("newPaket")
                    newPaket?.let {
                        listPaket.add(0, it)
                        Log.e("PaketLaundryFragment", "Added paket: $it")
                        adapter.notifyItemInserted(0)
                        Log.e("PaketLaundryFragment", "List paket: $listPaket")
                        binding.rvPaketLaundry.scrollToPosition(0)
                        Log.e("PaketLaundryFragment", "Scrolled to position 0")
                    }
                }
                REQUEST_CODE_EDIT -> {
                    val updatedPaket: PaketLaundryModel? = data?.getParcelableExtra("updatedPaket")
                    Log.e("PaketLaundryFragment", "Updated paket: $updatedPaket")
                    updatedPaket?.let {
                        val index = listPaket.indexOfFirst {
                            paket -> paket.package_laundry_id == updatedPaket.package_laundry_id
                        }
                        Log.e("PaketLaundryFragment", "Index: $index")
                        if (index != -1) {
                            listPaket[index] = updatedPaket
                            Log.e("PaketLaundryFragment", "Updated paket: $updatedPaket")
                            adapter.notifyItemChanged(index)
                            Log.e("PaketLaundryFragment", "List paket: $listPaket")
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
