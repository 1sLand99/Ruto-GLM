package com.rosan.ruto.autoglm.script

data class RutoToken(
    val type: Type,
    val value: String
) {
    enum class Type {
        IDENTIFIER, // id

        PAREN_L, // (
        PAREN_R, // )

        BRACKET_L, // [
        BRACKET_R, // ]

        COMMA, // ,

        EQUALS, // =
        NUMBER, // 123
        STRING, // "abc"
    }
}