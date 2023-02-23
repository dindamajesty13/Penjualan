package com.majesty.penjualan.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SaleDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
        db.execSQL(SQL_CREATE_PRODUCT)
        db.execSQL(SQL_CREATE_CART)
        db.execSQL(SQL_CREATE_TRANSACTION)
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES)
        db.execSQL(SQL_DELETE_PRODUCT)
        db.execSQL(SQL_DELETE_CART)
        db.execSQL(SQL_DELETE_TRANSACTION)
        onCreate(db)
    }
    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "penjualan.db"
        private const val SQL_CREATE_PRODUCT =
            "CREATE TABLE ${SaleContract.ProductEntry.TABLE_NAME} (" +
                    "${SaleContract.ProductEntry.COLUMN_PRODUCT_CODE} TEXT ," +
                    "${SaleContract.ProductEntry.COLUMN_PRODUCT_NAME} TEXT ," +
                    "${SaleContract.ProductEntry.COLUMN_PRODUCT_PRICE} NUMERIC ," +
                    "${SaleContract.ProductEntry.COLUMN_CURRENCY} TEXT ," +
                    "${SaleContract.ProductEntry.COLUMN_DISCOUNT} NUMERIC ," +
                    "${SaleContract.ProductEntry.COLUMN_DIMENSION} TEXT ," +
                    "${SaleContract.ProductEntry.COLUMN_UNIT} TEXT , " +
                    "${SaleContract.ProductEntry.COLUMN_IMAGE} TEXT)"
        private const val SQL_CREATE_ENTRIES =
            "CREATE TABLE ${SaleContract.LoginEntry.TABLE_NAME} (" +
                    "${SaleContract.LoginEntry.COLUMN_NAME_USER} TEXT ," +
                    "${SaleContract.LoginEntry.COLUMN_USERNAME} TEXT ," +
                    "${SaleContract.LoginEntry.COLUMN_NAME_PASSWORD} TEXT)"
        private const val SQL_CREATE_CART =
            "CREATE TABLE ${SaleContract.CartEntry.TABLE_NAME} (" +
                    "${SaleContract.CartEntry.COLUMN_PRODUCT_CODE} TEXT ," +
                    "${SaleContract.CartEntry.COLUMN_NAME_USER} TEXT ," +
                    "${SaleContract.CartEntry.COLUMN_QUANTITY} NUMERIC)"
        private const val SQL_CREATE_TRANSACTION =
            "CREATE TABLE ${SaleContract.TransactionEntry.TABLE_NAME} (" +
                    "${SaleContract.TransactionEntry.COLUMN_TRANSACTION_CODE} INTEGER PRIMARY KEY AUTOINCREMENT ," +
                    "${SaleContract.TransactionEntry.COLUMN_PRODUCT_CODE} TEXT ," +
                    "${SaleContract.TransactionEntry.COLUMN_NAME_USER} TEXT ," +
                    "${SaleContract.TransactionEntry.COLUMN_QUANTITY} NUMERIC ," +
                    "${SaleContract.TransactionEntry.COLUMN_TOTAL_PRICE} NUMERIC ," +
                    "${SaleContract.TransactionEntry.COLUMN_DATE} TEXT)"
        private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${SaleContract.LoginEntry.TABLE_NAME}"
        private const val SQL_DELETE_PRODUCT = "DROP TABLE IF EXISTS ${SaleContract.ProductEntry.TABLE_NAME}"
        private const val SQL_DELETE_CART = "DROP TABLE IF EXISTS ${SaleContract.CartEntry.TABLE_NAME}"
        private const val SQL_DELETE_TRANSACTION = "DROP TABLE IF EXISTS ${SaleContract.TransactionEntry.TABLE_NAME}"
    }
}