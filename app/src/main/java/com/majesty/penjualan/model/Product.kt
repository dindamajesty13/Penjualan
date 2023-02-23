package com.majesty.penjualan.model

class Product internal constructor(
    var productCode: String,
    var productName: String,
    var price: Int,
    var currency: String,
    var discount: Int,
    var dimension: String,
    var unit: String,
    var image: String,
)