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

        val whatsAppUrlSilver = "https://wa.me/6281217147620?text=Hallo%20mimin%2C%0A%0Asaya%20tertarik%20untuk%20berlangganan%20Silver%20Membership.%20Bisa%20tolong%20informasikan%20cara%20daftar%20dan%20pembayaran%20untuk%20paket%20ini%20%F0%9F%98%8A%3F%0A%0ATerima%C2%A0kasih"
        val whatsAppUrlGold = "https://wa.me/6281217147620?text=Hallo%20mimin%2C%0A%0Asaya%20tertarik%20untuk%20berlangganan%20Gold%20Membership.%20Bisa%20tolong%20informasikan%20cara%20daftar%20dan%20pembayaran%20untuk%20paket%20ini%20%F0%9F%98%8A%3F%0A%0ATerima%C2%A0kasih"

        binding.btnSilver.setOnClickListener {
            openWhatsApp(whatsAppUrlSilver)
        }

        binding.btnGold.setOnClickListener {
            openWhatsApp(whatsAppUrlGold)
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