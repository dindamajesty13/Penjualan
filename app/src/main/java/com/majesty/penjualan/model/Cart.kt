package com.majesty.penjualan.model

class Cart internal constructor(
    var productCode: String,
    var user: String,
    var quantity: Int,
    var price: Int?,
    var discount: Int?,
    var productName: String?,
    var image: String?,
    var unit: String?
)