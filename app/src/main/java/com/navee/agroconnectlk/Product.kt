package com.navee.agroconnectlk

data class Product(
    var id: String = "",
    val name: String = "",
    val price: String = "",
    val quantity: String = "",
    var farmerId: String = "FARMER_ID_FIXED",
    var farmerName: String = "Agro Farmer",
    var farmerPhone: String = "" // Added farmerPhone
)
