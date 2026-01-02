package com.navee.agroconnectlk

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class BuyerOrderAdapter(
    private val orderList: MutableList<Order>
) : RecyclerView.Adapter<BuyerOrderAdapter.OrderViewHolder>() {

    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val crop: TextView = view.findViewById(R.id.txtCrop)
        val qty: TextView = view.findViewById(R.id.txtQty)
        val total: TextView = view.findViewById(R.id.txtTotal)
        val status: TextView = view.findViewById(R.id.txtStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_buyer_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orderList[position]

        holder.crop.text = order.cropName
        holder.qty.text = "Qty: ${order.quantity} (kg)" // Added (kg) here
        holder.total.text = "Total: Rs ${order.totalPrice}"
        holder.status.text = "Status: ${order.status}"

        // 👉 CLICK ITEM → OPEN INVOICE
        holder.itemView.setOnClickListener {

            if (order.status == "Accepted") {

                val intent = Intent(holder.itemView.context, InvoiceActivity::class.java)
                intent.putExtra("orderId", order.orderId)
                holder.itemView.context.startActivity(intent)

            } else {
                Toast.makeText(
                    holder.itemView.context,
                    "Invoice available after farmer accepts the order",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun getItemCount(): Int = orderList.size
}
