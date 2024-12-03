package com.nafis.moneylaundry.transaction

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.nafis.moneylaundry.databinding.ActivityNewTransactionBinding
import com.nafis.moneylaundry.sheet.AddOnsBottomSheetFragment
import com.nafis.moneylaundry.models.PaketLaundryModel

class NewTransactionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewTransactionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val paketLaundry  = intent.getParcelableExtra<PaketLaundryModel>("paketLaundry")
        paketLaundry?.let {
            binding.edtNamaPaket.setText(it.name)
            binding.tvTransaksiBaru.text = it.name

            binding.edtNamaPaket.isFocusable = false
            binding.edtNamaPaket.isClickable = false
            binding.edtNamaPaket.isCursorVisible = false

        }

        binding.tilAddOns.setOnClickListener {
            showAddonsBottomSheet()
        }

        binding.edtAddOns.setOnClickListener {
            showAddonsBottomSheet()
        }

        binding.edtOrderDate.setOnClickListener {
            showDatePickerDialog(binding.edtOrderDate)
        }
        binding.edtPickUpDate.setOnClickListener {
            showDatePickerDialog(binding.edtPickUpDate)
        }
        binding.ivBackButton.setOnClickListener {
            finish()
        }
        binding.btnBuatPaket.setOnClickListener {
            val intent = Intent(this, OrderDetailActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun showAddonsBottomSheet() {
        val bottomSheetFragment = AddOnsBottomSheetFragment()
        bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
    }

    @SuppressLint("DefaultLocale")
    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val date = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear)
                editText.setText(date)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }
}