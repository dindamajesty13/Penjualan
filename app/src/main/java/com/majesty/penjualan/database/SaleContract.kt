package com.majesty.penjualan.database

import android.provider.BaseColumns

object SaleContract {
    object LoginEntry : BaseColumns {
        const val TABLE_NAME = "login"
        const val COLUMN_NAME_USER = "name"
        const val COLUMN_USERNAME = "user"
        const val COLUMN_NAME_PASSWORD = "password"
    }
    object ProductEntry : BaseColumns {
        const val TABLE_NAME = "product"
        const val COLUMN_PRODUCT_CODE = "product_code"
        const val COLUMN_PRODUCT_NAME = "product_name"
        const val COLUMN_PRODUCT_PRICE = "price"
        const val COLUMN_CURRENCY = "currency"
        const val COLUMN_DISCOUNT = "discount"
        const val COLUMN_DIMENSION = "dimension"
        const val COLUMN_UNIT = "unit"
        const val COLUMN_IMAGE = "image"
    }
    object CartEntry: BaseColumns {
        const val TABLE_NAME = "cart"
        const val COLUMN_PRODUCT_CODE = "product_code"
        const val COLUMN_NAME_USER = "user"
        const val COLUMN_QUANTITY = "quantity"
    }
    object TransactionEntry: BaseColumns {
        const val TABLE_NAME = "history_transaction"
        const val COLUMN_TRANSACTION_CODE = "transaction_code"
        const val COLUMN_PRODUCT_CODE = "product_code"
        const val COLUMN_NAME_USER = "user"
        const val COLUMN_QUANTITY = "quantity"
        const val COLUMN_TOTAL_PRICE = "total_price"
        const val COLUMN_DATE = "transaction_date"
    }
}