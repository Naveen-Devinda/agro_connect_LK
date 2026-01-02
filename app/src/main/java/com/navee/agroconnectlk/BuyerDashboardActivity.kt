package com.navee.agroconnectlk

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
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
    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapter: ProductAdapter
    private val productList = ArrayList<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 🔐 Safety check
        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_buyer_dashboard)

        // ---------------- RecyclerView ----------------
        val recycler = findViewById<RecyclerView>(R.id.recyclerProducts)
        recycler.layoutManager = LinearLayoutManager(this)

        adapter = ProductAdapter(
            productList,
            onEdit = null,
            onDelete = null,
            onItemClick = { product ->
                val intent = Intent(this, ProductDetailsActivity::class.java)
                intent.putExtra("id", product.id)
                intent.putExtra("name", product.name)
                intent.putExtra("price", product.price)
                startActivity(intent)
            }
        )

        recycler.adapter = adapter

        // ---------------- Refresh ----------------
        findViewById<ImageButton>(R.id.btnRefresh).setOnClickListener {
            loadCrops()
            Toast.makeText(this, "Market Refreshed", Toast.LENGTH_SHORT).show()
        }

        // ---------------- Bottom Navigation ----------------
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> {
                    // 👉 Redirect to Buyer's Order History
                    startActivity(Intent(this, BuyerOrdersActivity::class.java))
                    true
                }
                R.id.nav_market -> true
                R.id.nav_profile -> {
                    showLogoutDialog()
                    true
                }
                else -> false
            }
        }

        loadCrops()
    }

    private fun loadCrops() {
        db.collection("crops")
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
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load market", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Do you want to logout?")
            .setPositiveButton("Logout") { _, _ ->
                auth.signOut()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
