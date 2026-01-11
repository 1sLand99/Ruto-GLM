package com.rosan.ruto.ruto.script

import java.math.BigDecimal

data class RutoFunction(val action: suspend Scope.() -> RutoASTLiteral) {
    class Scope(val literals: List<RutoASTLiteral>) {
        val args = literals.map { it.unwrap() }

        fun literalOrNull(index: Int): RutoASTLiteral? = literals.getOrNull(index)

        fun literal(index: Int): RutoASTLiteral = literals[index]

        inline fun <reified T : Any?> argOrNull(index: Int): T {
            val p = literalOrNull(index)

            return when (T::class) {
                Boolean::class -> p?.boolean()
                BigDecimal::class -> p?.number()
                Int::class -> p?.integer()
                Long::class -> p?.long()
                Float::class -> p?.float()
                Double::class -> p?.double()
                String::class -> p?.string()
                List::class -> p?.list<Any>()
                else -> p?.unwrap()
            } as T
        }

        inline fun <reified T : Any> arg(index: Int) = argOrNull<T>(index)
    }
}