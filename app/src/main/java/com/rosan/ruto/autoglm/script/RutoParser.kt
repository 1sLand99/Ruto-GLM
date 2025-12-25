package com.rosan.ruto.autoglm.script

class RutoParser(private val tokens: List<RutoToken>) {
    private var cursor = 0

    private fun peek(i: Int = 0): RutoToken? = tokens.getOrNull(cursor + i)

    private fun peekNotNull(i: Int = 0): RutoToken =
        peek(i) ?: throw RutoParserException("Unexpected ending")

    private fun advance(): RutoToken? = tokens.getOrNull(cursor++)

    private fun advanceNotNull(): RutoToken =
        advance() ?: throw RutoParserException("Unexpected ending")

    private fun consume(type: RutoToken.Type): RutoToken {
        if (peek()?.type != type) throw RutoParserException("Expected $type but got ${peek()?.type}")
        return advanceNotNull()
    }

    private fun isAtEnd(): Boolean = peek() == null

    private fun notAtEnd(): Boolean = !isAtEnd()

    fun parse(): List<RutoASTNode> {
        val nodes = mutableListOf<RutoASTNode>()
        while (peek() != null) {
            nodes += parseBlock()
        }
        return nodes
    }

    fun parseBlock(): RutoASTNode {
        val type = peek()?.type
        return when {
            type == RutoToken.Type.NUMBER -> RutoASTNode.NumberValue(advanceNotNull().value.toInt())
            type == RutoToken.Type.STRING -> RutoASTNode.StringValue(advanceNotNull().value)
            type == RutoToken.Type.BRACKET_L -> parseList()
            type == RutoToken.Type.IDENTIFIER &&
                    peekNotNull(1).type == RutoToken.Type.PAREN_L -> parseFunctionCall()

            type == null -> throw RutoParserException("Unexpected ending")
            else -> throw RutoParserException("Unexpected token: ${peek()}")
        }
    }

    fun parseList(): RutoASTNode.ListValue {
        consume(RutoToken.Type.BRACKET_L)

        val elements = mutableListOf<RutoASTNode>()
        while (peekNotNull().type != RutoToken.Type.BRACKET_R) {
            elements.add(parseBlock())
            if (peek()?.type == RutoToken.Type.COMMA) advance()
        }

        consume(RutoToken.Type.BRACKET_R)
        return RutoASTNode.ListValue(elements)
    }

    // autoglm-phone 特殊的函数调用 :(
    // func(arg1="Hello", arg2="AutoGLM")
    fun parseFunctionCall(): RutoASTNode.FunctionCall {
        val name = consume(RutoToken.Type.IDENTIFIER).value
        consume(RutoToken.Type.PAREN_L)

        val arguments = hashMapOf<String, RutoASTNode>()
        while (peekNotNull().type != RutoToken.Type.PAREN_R) {
            val key = consume(RutoToken.Type.IDENTIFIER).value

            consume(RutoToken.Type.EQUALS)

            val value = parseBlock()
            arguments[key] = value
            if (peek()?.type == RutoToken.Type.COMMA) advance()
        }

        consume(RutoToken.Type.PAREN_R)
        return RutoASTNode.FunctionCall(name = name, arguments = arguments)
    }
}