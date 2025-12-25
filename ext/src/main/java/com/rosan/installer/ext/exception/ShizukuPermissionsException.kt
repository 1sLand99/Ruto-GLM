package com.rosan.installer.ext.exception

class ShizukuPermissionsException(
    override val message: String? = null,
    override val cause: Throwable? = null
) : RuntimeException(message, cause)