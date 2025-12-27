package com.rosan.ruto.ruto.script

import java.math.BigDecimal

sealed class RutoASTLiteral {
    object Void : RutoASTLiteral() {
        override fun unwrap() = Unit
    }

    data class BooleanValue(val value: Boolean) : RutoASTLiteral() {
        override fun unwrap() = value
    }

    data class NumberValue(val value: BigDecimal) : RutoASTLiteral() {
        override fun unwrap() = value
    }

    data class StringValue(val value: String) : RutoASTLiteral() {
        override fun unwrap() = value
    }

    data class ListValue(val elements: List<RutoASTLiteral>) : RutoASTLiteral() {
        override fun unwrap() = elements.map { it.unwrap() }
    }

    abstract fun unwrap(): Any

    inline fun <T> cast(action: (Any) -> T): T {
        return runCatching { action.invoke(unwrap()) }.getOrElse {
            throw RutoASTLiteralCastException(it.message ?: "")
        }
    }

    fun boolean() = cast {
        it as? BooleanValue ?: BooleanValue(
            it == "true" || number().compareTo(
                BigDecimal(0)
            ) == 0
        )
    }

    fun number() = cast { it as? BigDecimal ?: BigDecimal(it.toString()) }

    fun integer() = number().toInt()

    fun long() = number().toLong()

    fun float() = number().toFloat()

    fun double() = number().toDouble()

    fun string() = cast { it as String }

    fun <T : Any> list() = cast { it as List<T> }

    companion object {
        fun wrap(any: Any): RutoASTLiteral {
            return when (any) {
                is Boolean -> BooleanValue(any)
                is BigDecimal -> NumberValue(any)
                is Int -> NumberValue(any.toBigDecimal())
                is Long -> NumberValue(any.toBigDecimal())
                is Float -> NumberValue(any.toBigDecimal())
                is Double -> NumberValue(any.toBigDecimal())
                is String -> StringValue(any)
                is List<*> -> ListValue(any.map {
                    if (it == null) throw RutoASTLiteralCastException("null not supported by the script")
                    wrap(it)
                })

                is Unit -> Void

                else -> throw RutoASTLiteralCastException("${any::class.java}($any) not supported by the script")
            }
        }
    }
}