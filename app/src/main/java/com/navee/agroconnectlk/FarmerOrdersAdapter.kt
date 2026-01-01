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
                holder.status.setTextColor(Color.parseColor("#2E7D32")) // Green
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

        // ACCEPT ORDER
        holder.btnAccept.setOnClickListener {
            updateStatus(order.orderId, "Accepted")
        }

        // REJECT ORDER
        holder.btnReject.setOnClickListener {
            updateStatus(order.orderId, "Rejected")
        }

        // VIEW INVOICE
        holder.btnInvoice.setOnClickListener {
            val intent = Intent(context, InvoiceActivity::class.java)
            intent.putExtra("orderId", order.orderId)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = orderList.size

    private fun updateStatus(orderId: String, status: String) {
        if (orderId.isEmpty()) return
        
        db.collection("orders")
            .document(orderId)
            .update("status", status)
            .addOnSuccessListener {
                Toast.makeText(context, "Order $status", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to update order", Toast.LENGTH_SHORT).show()
            }
    }
}
