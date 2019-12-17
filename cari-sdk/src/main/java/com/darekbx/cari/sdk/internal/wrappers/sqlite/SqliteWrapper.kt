package com.darekbx.cari.sdk.internal.wrappers.sqlite

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.SystemClock
import com.darekbx.cari.sdk.internal.model.SqliteResultWrapper

internal class SqliteWrapper(val context: Context) {

    companion object val DEFAULT_LIMIT = 50

    fun Double.format(digits: Int) = "%.${digits}f".format(this)

    class DatabaseHelper(context: Context, databaseName: String) :
        SQLiteOpenHelper(context, databaseName, null, 1) {
        override fun onCreate(db: SQLiteDatabase?) {}
        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {}
    }

    fun execute(database: String, query: String): SqliteResultWrapper? {
        val helper = DatabaseHelper(context, database)
        helper.writableDatabase.use { db ->
            val startTime = SystemClock.elapsedRealtime()
            db.rawQuery(query, null)?.use { cursor ->
                val cursorCount = cursor.count
                val data = when (cursorCount) {
                    0 -> null
                    else -> printCursor(cursor)
                }
                return SqliteResultWrapper(data, obtainSummary(cursorCount, startTime))
            } ?: return SqliteResultWrapper(null, obtainAffectedRows(db))
        }
    }

    private fun printCursor(cursor: Cursor): List<List<String>> {
        var cursorData = mutableListOf<List<String>>()

        if (cursor.moveToFirst()) {
            val columnsList = obtainColumnsList(cursor)
            cursorData.add(columnsList)

            val rowsToFetch = Math.min(cursor.count - 1, DEFAULT_LIMIT)
            var rowIndex = 0

            do {
                val row = readRow(cursor)
                cursorData.add(row)
            } while (cursor.moveToNext() && rowIndex++ < rowsToFetch)
        }

        return cursorData
    }

    private fun readRow(cursor: Cursor) =
        mutableListOf<String>().also {
            (0..cursor.columnCount - 1).forEach { columnIndex ->
                when (cursor.getType(columnIndex)) {
                    Cursor.FIELD_TYPE_STRING -> it.add(cursor.getString(columnIndex))
                    Cursor.FIELD_TYPE_INTEGER -> it.add("${cursor.getInt(columnIndex)}")
                    Cursor.FIELD_TYPE_FLOAT -> it.add("${cursor.getFloat(columnIndex)}")
                    Cursor.FIELD_TYPE_BLOB -> {
                        val length = cursor.getBlob(columnIndex).size
                        it.add("BLOB, $length bytes")
                    }
                    Cursor.FIELD_TYPE_NULL -> it.add("NULL")
                    else -> it.add("Unknown")
                }
            }
        }

    private fun obtainColumnsList(cursor: Cursor) =
        mutableListOf<String>().also {
            (0..cursor.columnCount - 1).forEach { columnIndex ->
                it.add(cursor.getColumnName(columnIndex))
            }
        }

    private fun obtainSummary(count: Int, startTime: Long): String {
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