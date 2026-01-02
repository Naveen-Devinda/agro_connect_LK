package com.navee.agroconnectlk

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class BuyerOrdersActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: BuyerOrderAdapter
    private val orderList = ArrayList<Order>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buyer_orders)

        // ---------------- UI ----------------
        recycler = findViewById(R.id.recyclerBuyerOrders)
        recycler.layoutManager = LinearLayoutManager(this)

        adapter = BuyerOrderAdapter(orderList)
        recycler.adapter = adapter

        loadBuyerOrders()
    }

    private fun loadBuyerOrders() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) return

        val buyerId = user.uid // Actual logged-in buyer ID

        db.collection("orders")
            .whereEqualTo("buyerId", buyerId)
            .addSnapshotListener { value, _ ->
                orderList.clear()
                value?.forEach { doc ->
                    val order = doc.toObject(Order::class.java)
                    order.orderId = doc.id
                    orderList.add(order)
                }
                adapter.notifyDataSetChanged()
            }
    }
}
