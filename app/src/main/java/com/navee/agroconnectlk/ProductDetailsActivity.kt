package com.navee.agroconnectlk

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ProductDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)

        val name = intent.getStringExtra("name")
        val price = intent.getStringExtra("price")
        val qty = intent.getStringExtra("qty")

        findViewById<TextView>(R.id.txtName).text = name
        findViewById<TextView>(R.id.txtPrice).text = price
        findViewById<TextView>(R.id.txtQty).text = qty
    }
}
