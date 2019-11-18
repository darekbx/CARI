package com.darekbx.cari.internal.model

internal class CommandWrapper(
    val resource: String,
    val command: String,
    val arguments: Array<Argument>)