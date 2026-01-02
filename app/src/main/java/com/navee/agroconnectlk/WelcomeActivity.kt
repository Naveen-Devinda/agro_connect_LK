package com.navee.agroconnectlk

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        Handler(Looper.getMainLooper()).postDelayed({
            checkLogin()
        }, 5000) // 5 seconds splash
    }

    private fun checkLogin() {
        val user = FirebaseAuth.getInstance().currentUser

        if (user == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            checkUserRole(user.uid)
        }
    }

    private fun checkUserRole(uid: String) {
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                val role = doc.getString("role")

                if (role == "Farmer") {
                    startActivity(Intent(this, FarmerDashboardActivity::class.java))
                } else {
                    startActivity(Intent(this, BuyerDashboardActivity::class.java))
                }
                finish()
            }
            .addOnFailureListener {
                // If failed to fetch role, go to Login
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
    }
}
