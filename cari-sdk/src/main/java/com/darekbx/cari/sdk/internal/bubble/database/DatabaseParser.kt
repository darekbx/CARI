package com.darekbx.cari.sdk.internal.bubble.database

import android.content.Context
import com.darekbx.cari.sdk.internal.bubble.database.model.DatabaseItem
import com.darekbx.cari.sdk.internal.wrappers.sqlite.SqliteWrapper

class DatabaseParser(val context: Context) {

    fun readDatabases(): List<DatabaseItem> {
        val sqliteWrapper = SqliteWrapper(context)
        return context
            .databaseList()
            .map { database ->
                val tables = sqliteWrapper.listTables(database)
                DatabaseItem(database, tables)
            }
    }
}