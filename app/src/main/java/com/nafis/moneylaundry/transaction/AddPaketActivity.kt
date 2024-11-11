package com.nafis.moneylaundry.transaction

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.nafis.moneylaundry.R
import com.nafis.moneylaundry.databinding.ActivityAddPaketBinding
import com.nafis.moneylaundry.fragments.PaketLaundryFragment

class AddPaketActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddPaketBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPaketBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBackButton.setOnClickListener {
            onBackPressed()
        }

       binding.btnBuatPaket.setOnClickListener {
           val fragment = PaketLaundryFragment()
           val fragmentTransaction = supportFragmentManager.beginTransaction()
           fragmentTransaction.replace(R.id.frame_layout, fragment)
           fragmentTransaction.addToBackStack(null)
           fragmentTransaction.commit()
       }
    }
}