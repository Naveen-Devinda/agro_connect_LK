package com.navee.agroconnectlk

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProductAdapter(
    private val productList: List<Product>,
    private val onItemClick: ((Product) -> Unit)? = null,
    private val onEdit: ((Product) -> Unit)? = null,
    private val onDelete: ((Product) -> Unit)? = null
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.txtName)
        val price: TextView = view.findViewById(R.id.txtPrice)
        val qty: TextView = view.findViewById(R.id.txtQty)
        val farmerName: TextView = view.findViewById(R.id.txtFarmerName)
        val farmerPhone: TextView = view.findViewById(R.id.txtFarmerPhone)
        val createdAt: TextView = view.findViewById(R.id.txtCreatedAt)
        val btnEdit: Button = view.findViewById(R.id.btnEdit)
        val btnDelete: Button = view.findViewById(R.id.btnDelete)
        val layoutActions: LinearLayout = view.findViewById(R.id.layoutActions)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]

        holder.name.text = product.name
        holder.price.text = "Price: ${product.price}"
        holder.qty.text = "Qty: ${product.quantity} (kg)"
        holder.farmerName.text = "Farmer: ${product.farmerName}"
        holder.farmerPhone.text = "Contact: ${product.farmerPhone}"
        holder.createdAt.text = "Listed on: ${product.createdAt}"

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(product)
        }

        // Show/Hide buttons based on dashboard type
        if (onEdit != null || onDelete != null) {
            holder.layoutActions.visibility = View.VISIBLE
            
            if (onEdit != null) {
                holder.btnEdit.visibility = View.VISIBLE
                holder.btnEdit.setOnClickListener { onEdit.invoke(product) }
            } else {
                holder.btnEdit.visibility = View.GONE
            }

            if (onDelete != null) {
                holder.btnDelete.visibility = View.VISIBLE
                holder.btnDelete.setOnClickListener { onDelete.invoke(product) }
            } else {
                holder.btnDelete.visibility = View.GONE
            }
        } else {
            holder.layoutActions.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = productList.size
}
