package com.darekbx.cari.sdk.internal.model

internal class CommandWrapper(
    val resource: String,
    val command: String,
    val arguments: MutableList<Argument>)