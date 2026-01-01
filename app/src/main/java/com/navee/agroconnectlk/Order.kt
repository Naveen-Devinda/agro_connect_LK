package com.navee.agroconnectlk

data class Order(
    var orderId: String = "",
    var cropId: String = "",
    var cropName: String = "",
    var price: String = "",
    var quantity: String = "",
    var totalPrice: String = "",
    var buyerId: String = "",
    var buyerName: String = "Buyer",
    var farmerId: String = "",
    var farmerName: String = "",
    var orderDate: String = "",
    var status: String = "Pending"
)
