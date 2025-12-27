package com.rosan.ruto.ruto.script

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

        ADD, // +
        SUB, // -
        MUL, // *
        DIV, // /
        MOD, // %
        EQUALS, // =

        NUMBER, // 123
        STRING, // "abc"
    }
}