package com.darekbx.cari.testapplication.testdata.defaultdatabase

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TestDefaultDatabase {

    companion object {
        val DB_NAME = "default_db"
        val TABLE_NAME = "expenses"
        val AMOUNT_COLUMN = "amount"
        val DESCRIPTION_COLUMN = "description"
    }

    class TestDbHelper(context: Context): SQLiteOpenHelper(context, DB_NAME,null, 1) {
        override fun onCreate(db: SQLiteDatabase?) {
            db?.execSQL("""CREATE TABLE $TABLE_NAME (
                ${BaseColumns._ID} INTEGER PRIMARY KEY, 
                $AMOUNT_COLUMN REAL NOT NULL, 
                $DESCRIPTION_COLUMN TEXT DEFAULT NULL
            )""")
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        }
    }

    fun createTestDatabase(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val db = TestDbHelper(context).writableDatabase

            with(db) {
                insert(TABLE_NAME, null, ContentValues().apply {
                    put(AMOUNT_COLUMN, 10.25)
                    put(DESCRIPTION_COLUMN, "Candies")
                })
                insert(TABLE_NAME, null, ContentValues().apply {
                    put(AMOUNT_COLUMN, 520.99)
                    put(DESCRIPTION_COLUMN, "Clothes")
                })
                insert(TABLE_NAME, null, ContentValues().apply {
                    put(AMOUNT_COLUMN, 0.99)
                    put(DESCRIPTION_COLUMN, "Bus ticket")
                })
                insert(TABLE_NAME, null, ContentValues().apply {
                    put(AMOUNT_COLUMN, 100.00)
                })

                close()
            }
        }
    }
}