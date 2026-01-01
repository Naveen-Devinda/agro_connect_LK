package com.navee.agroconnectlk

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class InvoiceActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invoice)

        val orderId = intent.getStringExtra("orderId") ?: return

        val txtOrderId = findViewById<TextView>(R.id.txtOrderId)
        val txtBuyer = findViewById<TextView>(R.id.txtBuyer)
        val txtFarmer = findViewById<TextView>(R.id.txtFarmer)
        val txtCrop = findViewById<TextView>(R.id.txtCrop)
        val txtQty = findViewById<TextView>(R.id.txtQty)
        val txtPrice = findViewById<TextView>(R.id.txtPrice)
        val txtTotal = findViewById<TextView>(R.id.txtTotal)
        val txtStatus = findViewById<TextView>(R.id.txtStatus)
        val txtDate = findViewById<TextView>(R.id.txtDate)

        db.collection("orders")
            .document(orderId)
            .get()
            .addOnSuccessListener { doc ->

                txtOrderId.text = "Order ID: ${doc.id}"
                txtBuyer.text = "Buyer: ${doc.getString("buyerName")}"
                txtFarmer.text = "Farmer: ${doc.getString("farmerName")}"
                txtCrop.text = "Crop: ${doc.getString("cropName")}"
                txtQty.text = "Quantity: ${doc.getString("quantity")}"
                txtPrice.text = "Price: Rs ${doc.getString("price")}"
                txtTotal.text = "Total: Rs ${doc.getString("totalPrice")}"
                txtStatus.text = "Status: ${doc.getString("status")}"
                txtDate.text = "Date: ${doc.getString("orderDate")}"
            }
    }
}
