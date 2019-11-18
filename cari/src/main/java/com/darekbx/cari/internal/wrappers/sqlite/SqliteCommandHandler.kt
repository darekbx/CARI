package com.darekbx.cari.internal.wrappers.sqlite

import android.content.Context
import com.darekbx.cari.internal.wrappers.BaseCommandHandler

internal class SqliteCommandHandler(context: Context) : BaseCommandHandler(context) {

    override fun handleCommand(command: String): Any {
        return false
    }
}