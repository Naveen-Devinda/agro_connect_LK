package com.navee.agroconnectlk

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source

class FarmerOrdersActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: FarmerOrdersAdapter
    private val orderList = ArrayList<Order>()
    private val db = FirebaseFirestore.getInstance()
    private lateinit var txtNoOrders: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_farmer_orders)

        recycler = findViewById(R.id.recyclerOrders)
        txtNoOrders = findViewById(R.id.txtNoOrders)
        
        recycler.layoutManager = LinearLayoutManager(this)
        adapter = FarmerOrdersAdapter(this, orderList)
        recycler.adapter = adapter

        loadOrders()
    }

    private fun loadOrders() {
        val farmerId = "FARMER_ID_FIXED" 

        // Snapshot listener for real-time updates
        db.collection("orders")
            .whereEqualTo("farmerId", farmerId)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.e("FarmerOrders", "Listen failed.", error)
                    return@addSnapshotListener
                }

                orderList.clear()
                if (value != null && !value.isEmpty) {
                    for (doc in value) {
                        val order = doc.toObject(Order::class.java)
                        order.orderId = doc.id
                        orderList.add(order)
                    }
                    txtNoOrders.visibility = View.GONE
                } else {
                    txtNoOrders.visibility = View.VISIBLE
                }
                adapter.notifyDataSetChanged()
            }
    }
}
