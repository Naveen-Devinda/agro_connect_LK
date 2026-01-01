package com.navee.agroconnectlk



import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView

class OrderAdapter(
    private val orderList: MutableList<Order>,
    private val onAccept: (Order) -> Unit,
    private val onReject: (Order) -> Unit
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cropName: TextView = view.findViewById(R.id.txtCropName)
        val qty: TextView = view.findViewById(R.id.txtQty)
        val total: TextView = view.findViewById(R.id.txtTotal)
        val status: TextView = view.findViewById(R.id.txtStatus)
        val btnAccept: Button = view.findViewById(R.id.btnAccept)
        val btnReject: Button = view.findViewById(R.id.btnReject)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orderList[position]

        holder.cropName.text = order.cropName
        holder.qty.text = "Qty: ${order.quantity}"
        holder.total.text = "Total: Rs ${order.totalPrice}"
        holder.status.text = "Status: ${order.status}"

        // Hide buttons if already processed
        if (order.status != "Pending") {
            holder.btnAccept.visibility = View.GONE
            holder.btnReject.visibility = View.GONE
        }

        holder.btnAccept.setOnClickListener { onAccept(order) }
        holder.btnReject.setOnClickListener { onReject(order) }
    }

    override fun getItemCount(): Int = orderList.size
}
