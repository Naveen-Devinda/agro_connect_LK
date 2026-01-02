package com.navee.agroconnectlk

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FarmerDashboardActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: ProductAdapter
    private lateinit var etSearch: EditText
    private lateinit var spinnerFilter: Spinner

    private val productList = ArrayList<Product>()
    private val fullList = ArrayList<Product>()

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔐 Safety check (auto login protection)
        if (auth.currentUser == null) {
            goToWelcome()
            return
        }

        setContentView(R.layout.activity_farmer_dashboard)

        // ---------------- UI ----------------
        recycler = findViewById(R.id.recyclerProducts)
        etSearch = findViewById(R.id.etSearch)
        spinnerFilter = findViewById(R.id.spinnerFilter)

        recycler.layoutManager = LinearLayoutManager(this)

        // ---------------- Adapter ----------------
        adapter = ProductAdapter(
            productList,
            onItemClick = null,

            onEdit = { product ->
                val intent = Intent(this, AddEditCropActivity::class.java)
                intent.putExtra("id", product.id)
                intent.putExtra("name", product.name)
                intent.putExtra("price", product.price)
                intent.putExtra("qty", product.quantity)
                startActivity(intent)
            },

            onDelete = { product ->
                showDeleteDialog(product.id)
            }
        )

        recycler.adapter = adapter

        // ---------------- Spinner ----------------
        val filterAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.filter_options,
            android.R.layout.simple_spinner_item
        )
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFilter.adapter = filterAdapter

        // ---------------- Search ----------------
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterCrops()
            }
        })

        // ---------------- Filter ----------------
        spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                filterCrops()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // ---------------- Buttons ----------------
        findViewById<Button>(R.id.btnAddCrop).setOnClickListener {
            startActivity(Intent(this, AddEditCropActivity::class.java))
        }

        findViewById<Button>(R.id.btnViewOrders).setOnClickListener {
            startActivity(Intent(this, FarmerOrdersActivity::class.java))
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

        loadCrops()
    }

    override fun onResume() {
        super.onResume()
        loadCrops()
    }

    // ---------------- LOAD CROPS ----------------
    private fun loadCrops() {
        db.collection("crops")
            .get()
            .addOnSuccessListener { result ->
                fullList.clear()
                productList.clear()

                for (doc in result) {
                    val product = doc.toObject(Product::class.java)
                    product.id = doc.id
                    fullList.add(product)
                }

                productList.addAll(fullList)
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load crops", Toast.LENGTH_SHORT).show()
            }
    }

    // ---------------- SEARCH + FILTER ----------------
    private fun filterCrops() {
        val query = etSearch.text.toString().lowercase()
        val filterPos = spinnerFilter.selectedItemPosition

        var filtered = fullList.filter {
            it.name.lowercase().contains(query)
        }

        filtered = when (filterPos) {
            1 -> filtered.sortedBy { it.price.toDoubleOrNull() ?: 0.0 }
            2 -> filtered.sortedByDescending { it.price.toDoubleOrNull() ?: 0.0 }
            else -> filtered
        }

        productList.clear()
        productList.addAll(filtered)
        adapter.notifyDataSetChanged()
    }

    // ---------------- DELETE ----------------
    private fun showDeleteDialog(productId: String) {
        AlertDialog.Builder(this)
            .setTitle("Delete Crop")
            .setMessage("Are you sure you want to delete this crop?")
            .setPositiveButton("Delete") { _, _ ->
                db.collection("crops").document(productId)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Crop deleted", Toast.LENGTH_SHORT).show()
                        loadCrops()
                    }
            }
            .setNegativeButton("Cancel", null)
            .show()
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
        goToWelcome()
    }

    private fun goToWelcome() {
        val intent = Intent(this, WelcomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
