package com.darekbx.cari.sdk.internal.wrappers.sqlite

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.SystemClock

internal class SqliteWrapper(val context: Context) {

    fun Double.format(digits: Int) = "%.${digits}f".format(this)

    class DatabaseHelper(context: Context, databaseName: String) :
        SQLiteOpenHelper(context, databaseName, null, 1) {
        override fun onCreate(db: SQLiteDatabase?) {}
        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {}
    }

    fun execute(database: String, query: String): String? {
        val helper = DatabaseHelper(context, database)
        helper.writableDatabase.use { db ->
            val startTime = SystemClock.elapsedRealtime()
            db.rawQuery(query, null)?.use { cursor ->
                return when (cursor.count) {
                    0 -> printSummary(0, startTime)
                    else -> printCursor(cursor, startTime)
                }
            } ?: return obtainAffectedRows(db)
        }
    }

    private fun printCursor(cursor: Cursor, startTime: Long): String {

        if (cursor.moveToFirst()) {

        }

        return printSummary(cursor.count, startTime)
    }

    private fun printSummary(count: Int, startTime: Long): String {
        val endTime = SystemClock.elapsedRealtime()
        val seconds = (endTime - startTime) / 1000.0
        return "$count rows is set <${seconds.format(2)} sec>"
    }

    private fun obtainAffectedRows(db: SQLiteDatabase): String? {
        val affectedRowsColumn = "affected_row_count"
        db.rawQuery("SELECT changes() AS $affectedRowsColumn", null)?.use {
            if (it.count > 0 && it.moveToFirst()) {
                val count = it.getLong(it.getColumnIndex(affectedRowsColumn))
                return "Affected rows: $count"
            }
        }
        return null
    }
}