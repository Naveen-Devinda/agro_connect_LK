package com.navee.agroconnectlk

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class ProductDetailsActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private var farmerId = "FARMER_ID_FIXED"
    private var farmerName = "Agro Farmer"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)

        val txtName = findViewById<TextView>(R.id.txtName)
        val txtPrice = findViewById<TextView>(R.id.txtPrice)
        val etQty = findViewById<EditText>(R.id.etOrderQty)
        val btnOrder = findViewById<Button>(R.id.btnOrder)

        val cropId = intent.getStringExtra("id") ?: ""
        val cropName = intent.getStringExtra("name") ?: ""
        val price = intent.getStringExtra("price") ?: ""
        
        if (cropId.isNotEmpty()) {
            db.collection("crops").document(cropId).get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        farmerId = doc.getString("farmerId") ?: "FARMER_ID_FIXED"
                        farmerName = doc.getString("farmerName") ?: "Agro Farmer"
                    }
                }
        }

        txtName.text = cropName
        txtPrice.text = "Price: $price"

        btnOrder.setOnClickListener {
            
            if (!isNetworkAvailable()) {
                Toast.makeText(this, "No Internet Connection! Order will only sync when online.", Toast.LENGTH_LONG).show()
            }

            val qty = etQty.text.toString().trim()
            if (qty.isEmpty()) {
                Toast.makeText(this, "Enter quantity", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val priceValue = price.replace(Regex("[^0-9.]"), "").toDoubleOrNull() ?: 0.0
            val qtyValue = qty.toIntOrNull() ?: 0
            
            if (qtyValue <= 0) {
                Toast.makeText(this, "Enter valid quantity", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val total = priceValue * qtyValue
            val date = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())

            val order = Order(
                orderId = "",
                cropId = cropId,
                cropName = cropName,
                price = price,
                quantity = qty,
                totalPrice = total.toString(),
                buyerId = "BUYER_ID_FIXED", 
                buyerName = "John Doe",
                farmerId = farmerId,
                farmerName = farmerName,
                status = "Pending",
                orderDate = date
            )

            db.collection("orders")
                .add(order)
                .addOnSuccessListener { doc ->
                    db.collection("orders").document(doc.id).update("orderId", doc.id)
                    Toast.makeText(this, "Order Placed Successfully!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nw = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }
}
