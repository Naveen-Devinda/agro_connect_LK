package com.navee.agroconnectlk

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentUser = auth.currentUser

        if (currentUser == null) {
            // ❌ User NOT logged in → go to Login
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            // ✅ User already logged in → check role
            val userId = currentUser.uid

            db.collection("users")
                .document(userId)
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
                    // fallback
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
        }
    }
}
