package com.rosan.installer.ext.exception

class RootNotWorkException(
    override val message: String? = null,
    override val cause: Throwable? = null
) : RuntimeException(message, cause)