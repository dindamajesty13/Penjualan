package com.majesty.penjualan.model

class Transaction internal constructor(
    var productCode: String,
    var user: String,
    var quantity: Int,
    var price: Int?,
    var discount: Int?,
    var productName: String?,
    var image: String?,
    var date: String,
    var unit: String
)