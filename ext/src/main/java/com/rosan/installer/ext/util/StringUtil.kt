package com.rosan.installer.ext.util

val String.fileName: String
    get() = this.substringAfterLast("/")

val String.fileExtension: String
    get() = this.fileName.substringAfterLast(".", "")

val String.fileNameWithoutExtension: String
    get() = this.fileName.substringBeforeLast(".")
