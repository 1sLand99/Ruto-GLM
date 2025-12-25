package com.rosan.ruto.autoglm.script

import kotlin.collections.get

class RutoLexer(private val input: String) {
    companion object {
        private val TOKEN_MAP = mapOf(
            '(' to RutoToken.Type.PAREN_L,
            ')' to RutoToken.Type.PAREN_R,
            '[' to RutoToken.Type.BRACKET_L,
            ']' to RutoToken.Type.BRACKET_R,
            ',' to RutoToken.Type.COMMA,
            '=' to RutoToken.Type.EQUALS
        ).mapValues { RutoToken(it.value, it.key.toString()) }
    }

    private var cursor = 0

    private val tokens = mutableListOf<RutoToken>()

    private fun peek(): Char? = input.getOrNull(cursor)

    private fun advance(): Char? = input.getOrNull(cursor++)

    private fun advanceNotNull(): Char =
        advance() ?: throw RutoLexerException("Unexpected ending")

    private fun read(length: Int): String = input.substring(cursor, length)

    private fun consume(c: Char): Char {
        if (peek() != c) throw RutoParserException("Expected $c but got ${peek()}")
        return advanceNotNull()
    }

    private fun isAtEnd(): Boolean = peek() == null

    private fun notAtEnd(): Boolean = !isAtEnd()

    fun tokenize(): List<RutoToken> {
        while (peek() != null) {
            when {
                peek()!!.isWhitespace() -> cursor++

                handleSingleCharToken() -> {}

                peek()!!.isDigit() -> {
                    tokens.add(RutoToken(RutoToken.Type.NUMBER, readNumber()))
                }

                peek()!!.isLetter() -> {
                    tokens.add(RutoToken(RutoToken.Type.IDENTIFIER, readIdentifier()))
                }

                peek() == '"' -> tokens.add(RutoToken(RutoToken.Type.STRING, readString()))

                else -> throw RutoLexerException("unsupported char: ${peek()}")
            }
        }
        return tokens
    }

    private fun handleSingleCharToken(): Boolean {
        tokens.add(TOKEN_MAP[peek()] ?: return false)
        cursor++
        return true
    }

    private fun readNumber(): String {
        val start = cursor
        while (peek()?.isDigit() == true) cursor++
        return input.substring(start, cursor)
    }

    private fun readIdentifier(): String {
        val start = cursor
        while (peek()?.isLetterOrDigit() == true) cursor++
        return input.substring(start, cursor)
    }

    private fun readString(): String {
        consume('"')

        val builder = StringBuilder()
        while (notAtEnd() && peek() != '"') {
            val c = advance()
            if (c == '\\') {
                when (val escape = advance()) {
                    '\\' -> builder.append('\\')
                    '"' -> builder.append('"')
                    'n' -> builder.append('\n')
                    'r' -> builder.append('\r')
                    't' -> builder.append('\t')
                    'b' -> builder.append('\b')

                    'u' -> {
                        val hex = read(4)
                        val unicode = runCatching { hex.toInt(0xf).toChar() }.getOrElse {
                            throw RutoLexerException("Incomplete unicode escape: $hex")
                        }
                        builder.append(unicode)
                    }

                    else -> throw RutoLexerException("Illegal escape character: \\$escape")
                }
            } else builder.append(c)
        }

        consume('"')
        return builder.toString()
    }
}