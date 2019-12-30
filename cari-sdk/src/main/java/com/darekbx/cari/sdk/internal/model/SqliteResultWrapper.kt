package com.darekbx.cari.sdk.internal.model

class SqliteResultWrapper(
    val result: Any?,
    val summary: String?,
    val limitedRows: Int = 0)