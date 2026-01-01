package com.navee.agroconnectlk

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class BuyerDashboardActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buyer_dashboard)

        // ---------------- RecyclerView ----------------
        val recycler = findViewById<RecyclerView>(R.id.recyclerProducts)
        recycler.layoutManager = LinearLayoutManager(this)

        val productList = ArrayList<Product>()
        val adapter = ProductAdapter(
            productList,
            onEdit = null,      // Buyer cannot edit
            onDelete = null,    // Buyer cannot delete

            // 👆 Click product
            onItemClick = { product ->
                val intent = Intent(this, ProductDetailsActivity::class.java)
                intent.putExtra("id", product.id)
                intent.putExtra("name", product.name)
                intent.putExtra("price", product.price)
                startActivity(intent)
            }
        )

        recycler.adapter = adapter

        // ---------------- Load Crops ----------------
        FirebaseFirestore.getInstance()
            .collection("crops")
            .get()
            .addOnSuccessListener { result ->
                productList.clear()
                for (doc in result) {
                    val product = doc.toObject(Product::class.java)
                    product.id = doc.id
                    productList.add(product)
                }
                adapter.notifyDataSetChanged()
            }

        // ---------------- Bottom Navigation ----------------
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> true

                R.id.nav_market -> true

                R.id.nav_profile -> {
                    showLogoutDialog()
                    true
                }

                else -> false
            }
        }
    }

    // ---------------- LOGOUT ----------------
    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Do you want to logout?")
            .setPositiveButton("Logout") { _, _ ->
                logoutUser()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun logoutUser() {
        auth.signOut()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
