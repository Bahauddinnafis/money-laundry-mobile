package com.nafis.moneylaundry.transaction

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.nafis.moneylaundry.databinding.ActivityPremiumBinding

class PremiumActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPremiumBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPremiumBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val whatsAppUrl = "https://wa.me/6281217147620?text=%2ASaya+Ingin+Bergabung+Menjadi+member"

        binding.btnSilver.setOnClickListener {
            openWhatsApp(whatsAppUrl)
        }

        binding.btnGold.setOnClickListener {
            openWhatsApp(whatsAppUrl)
        }

        binding.ivBackButton.setOnClickListener {
            finish()
        }
    }

    @Suppress("SameParameterValue")
    private fun openWhatsApp(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.setPackage("com.whatsapp")
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "WhatsApp tidak ditemukan di perangkat ini", Toast.LENGTH_SHORT).show()
        }
    }
}