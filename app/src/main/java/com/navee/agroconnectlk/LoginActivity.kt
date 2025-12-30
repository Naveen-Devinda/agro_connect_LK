package com.navee.agroconnectlk

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private var userType = ""
    private val generatedOtp = "123456" // demo OTP

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btnFarmer = findViewById<Button>(R.id.btnFarmer)
        val btnBuyer = findViewById<Button>(R.id.btnBuyer)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val phone = findViewById<EditText>(R.id.etPhone)
        val otp = findViewById<EditText>(R.id.etOtp)

        btnFarmer.setOnClickListener {
            userType = "Farmer"
            Toast.makeText(this, "Farmer Selected", Toast.LENGTH_SHORT).show()
        }

        btnBuyer.setOnClickListener {
            userType = "Buyer"
            Toast.makeText(this, "Buyer Selected", Toast.LENGTH_SHORT).show()
        }

        btnLogin.setOnClickListener {
            if (userType.isEmpty()) {
                Toast.makeText(this, "Select Farmer or Buyer", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val phoneNumber = phone.text.toString().trim()
            if (phoneNumber.isEmpty()) {
                Toast.makeText(this, "Enter phone number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(this, "Sending OTP to $phoneNumber...", Toast.LENGTH_SHORT).show()

            // Disable button during process
            btnLogin.isEnabled = false

            // Simulate OTP auto verify after 2 seconds
            Handler(Looper.getMainLooper()).postDelayed({
                try {
                    otp.setText(generatedOtp)
                    Toast.makeText(this, "OTP Verified Automatically", Toast.LENGTH_LONG).show()

                    // Call the member function
                    loginSuccess()
                } catch (e: Exception) {
                    Toast.makeText(this, "Error during login: ${e.message}", Toast.LENGTH_SHORT).show()
                    btnLogin.isEnabled = true
                }
            }, 2000)
        }
    }

    private fun loginSuccess() {
        try {
            Toast.makeText(
                this,
                "Login Successful as $userType",
                Toast.LENGTH_LONG
            ).show()

            val intent = if (userType == "Farmer") {
                Intent(this, FarmerDashboardActivity::class.java)
            } else {
                Intent(this, BuyerDashboardActivity::class.java)
            }
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to open dashboard: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
