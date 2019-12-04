package com.darekbx.carisdk.internal.model

internal class CommandWrapper(
    val resource: String,
    val command: String,
    val arguments: Array<Argument>)