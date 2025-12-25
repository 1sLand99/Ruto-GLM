package com.rosan.installer.ext.exception

class TerminalNotWorkException(
    override val message: String? = null,
    override val cause: Throwable? = null
) : RuntimeException(message, cause)