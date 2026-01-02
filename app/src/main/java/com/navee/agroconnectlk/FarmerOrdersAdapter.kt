package com.navee.agroconnectlk

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class FarmerOrdersAdapter(
    private val context: Context,
    private val orderList: MutableList<Order>
) : RecyclerView.Adapter<FarmerOrdersAdapter.OrderViewHolder>() {

    private val db = FirebaseFirestore.getInstance()

    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val crop: TextView = view.findViewById(R.id.txtCrop)
        val qty: TextView = view.findViewById(R.id.txtQty)
        val total: TextView = view.findViewById(R.id.txtTotal)
        val status: TextView = view.findViewById(R.id.txtStatus)
        val btnAccept: Button = view.findViewById(R.id.btnAccept)
        val btnReject: Button = view.findViewById(R.id.btnReject)
        val btnInvoice: Button = view.findViewById(R.id.btnInvoice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_farmer_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orderList[position]

        holder.crop.text = order.cropName
        holder.qty.text = "Qty: ${order.quantity}"
        holder.total.text = "Total: Rs ${order.totalPrice}"
        holder.status.text = "Status: ${order.status}"

        // UI Logic based on Status
        when (order.status) {
            "Accepted" -> {
                holder.status.setTextColor(Color.parseColor("#2E7D32")) 
                holder.btnAccept.visibility = View.GONE
                holder.btnReject.visibility = View.GONE
                holder.btnInvoice.visibility = View.VISIBLE
            }
            "Rejected" -> {
                holder.status.setTextColor(Color.RED)
                holder.btnAccept.visibility = View.GONE
                holder.btnReject.visibility = View.GONE
                holder.btnInvoice.visibility = View.GONE
            }
            else -> {
                holder.status.setTextColor(Color.GRAY)
                holder.btnAccept.visibility = View.VISIBLE
                holder.btnReject.visibility = View.VISIBLE
                holder.btnInvoice.visibility = View.GONE
            }
        }

        holder.btnAccept.setOnClickListener {
            processOrder(order, "Accepted")
        }

        holder.btnReject.setOnClickListener {
            processOrder(order, "Rejected")
        }

        holder.btnInvoice.setOnClickListener {
            val intent = Intent(context, InvoiceActivity::class.java)
            intent.putExtra("orderId", order.orderId)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = orderList.size

    private fun processOrder(order: Order, newStatus: String) {
        if (order.orderId.isEmpty()) return

        if (newStatus == "Accepted") {
            // TRANSACTION: Deduct quantity and update status
            val cropRef = db.collection("crops").document(order.cropId)
            val orderRef = db.collection("orders").document(order.orderId)

            db.runTransaction { transaction ->
                val cropSnap = transaction.get(cropRef)
                val currentQty = (cropSnap.getString("quantity") ?: "0").toIntOrNull() ?: 0
                val orderedQty = order.quantity.toIntOrNull() ?: 0

                if (currentQty < orderedQty) {
                    throw Exception("Not enough stock to accept this order!")
                }

                transaction.update(cropRef, "quantity", (currentQty - orderedQty).toString())
                transaction.update(orderRef, "status", "Accepted")
                null
            }.addOnSuccessListener {
                Toast.makeText(context, "Order Accepted and Stock Updated", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener { e ->
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        } else {
            // Just update status to Rejected
            db.collection("orders").document(order.orderId)
                .update("status", "Rejected")
                .addOnSuccessListener {
                    Toast.makeText(context, "Order Rejected", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
