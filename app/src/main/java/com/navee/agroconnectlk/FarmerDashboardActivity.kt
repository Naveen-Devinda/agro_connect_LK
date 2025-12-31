package com.navee.agroconnectlk

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore

class FarmerDashboardActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: ProductAdapter
    private val productList = ArrayList<Product>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_farmer_dashboard)

        // ---------------- RecyclerView ----------------
        recycler = findViewById(R.id.recyclerProducts)
        recycler.layoutManager = LinearLayoutManager(this)

        adapter = ProductAdapter(
            productList,

            // ✏ EDIT CLICK
            onEdit = { product ->
                val intent = Intent(this, AddEditCropActivity::class.java)
                intent.putExtra("id", product.id)
                intent.putExtra("name", product.name)
                intent.putExtra("price", product.price)
                intent.putExtra("qty", product.quantity)
                startActivity(intent)
            },

            // ❌ DELETE CLICK
            onDelete = { product ->
                showDeleteDialog(product.id)
            }
        )

        recycler.adapter = adapter

        // Load crops first time
        loadCrops()

        // ---------------- Add Crop Button ----------------
        findViewById<Button>(R.id.btnAddCrop).setOnClickListener {
            startActivity(Intent(this, AddEditCropActivity::class.java))
        }

        // ---------------- Bottom Navigation ----------------
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show()
                R.id.nav_market -> Toast.makeText(this, "Market", Toast.LENGTH_SHORT).show()
                R.id.nav_profile -> Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show()
            }
            true
        }
    }

    // 🔄 Reload data when coming back from Edit/Add screen
    override fun onResume() {
        super.onResume()
        loadCrops()
    }

    // ---------------- LOAD CROPS ----------------
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
                Toast.makeText(this, "Failed to load crops", Toast.LENGTH_SHORT).show()
            }
    }

    // ---------------- DELETE CONFIRMATION ----------------
    private fun showDeleteDialog(cropId: String) {
        AlertDialog.Builder(this)
            .setTitle("Delete Crop")
            .setMessage("Are you sure you want to delete this crop?")
            .setPositiveButton("Yes") { _, _ ->
                deleteCrop(cropId)
            }
            .setNegativeButton("No", null)
            .show()
    }

    // ---------------- DELETE FROM FIREBASE ----------------
    private fun deleteCrop(cropId: String) {
        db.collection("crops")
            .document(cropId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Crop deleted", Toast.LENGTH_SHORT).show()
                loadCrops()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show()
            }
    }
}
