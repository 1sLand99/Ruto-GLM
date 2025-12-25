package com.rosan.installer.ext.util

fun Int.hasFlag(flag: Int) = flag and this == flag

fun Int.withFlags(vararg flags: Int): Int = flags.fold(this) { i, flag ->
    i or flag
}

fun Int.withoutFlags(vararg flags: Int) = flags.fold(this) { i, flag ->
    i and flag.inv()
}
