package com.rosan.ruto.ruto.script

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
            nodes += parseExp()
        }
        return nodes
    }

    fun parsePrimary(): RutoASTNode {
        val type = peek()?.type
        return when {
            type == RutoToken.Type.NUMBER -> RutoASTNode.NumberValue(
                advanceNotNull().value
            )

            type == RutoToken.Type.STRING -> RutoASTNode.StringValue(
                advanceNotNull().value
            )

            type == RutoToken.Type.PAREN_L -> {
                consume(RutoToken.Type.PAREN_L)
                val node = parseExp()
                consume(RutoToken.Type.PAREN_R)
                node
            }

            type == RutoToken.Type.BRACKET_L -> parseList()

            type == RutoToken.Type.IDENTIFIER && peekNotNull(1).type == RutoToken.Type.PAREN_L -> parseFunctionCall()

            type == null -> throw RutoParserException("Unexpected ending")
            else -> throw RutoParserException("Unexpected token: ${peek()}")
        }
    }

    fun parseList(): RutoASTNode.ListValue {
        consume(RutoToken.Type.BRACKET_L)

        val elements = mutableListOf<RutoASTNode>()
        while (peekNotNull().type != RutoToken.Type.BRACKET_R) {
            elements.add(parsePrimary())
            if (peek()?.type == RutoToken.Type.COMMA) advance()
        }

        consume(RutoToken.Type.BRACKET_R)
        return RutoASTNode.ListValue(elements)
    }

    // func(123, "abc", func([1, 2]))
    fun parseFunctionCall(): RutoASTNode.FunctionCall {
        val name = consume(RutoToken.Type.IDENTIFIER).value
        consume(RutoToken.Type.PAREN_L)

        val arguments = mutableListOf<RutoASTNode>()
        while (peekNotNull().type != RutoToken.Type.PAREN_R) {
            val value = parsePrimary()
            arguments += value
            if (peek()?.type == RutoToken.Type.COMMA) advance()
        }

        consume(RutoToken.Type.PAREN_R)
        return RutoASTNode.FunctionCall(name = name, arguments = arguments)
    }

    fun parseExpMul(): RutoASTNode {
        val map = mapOf<RutoToken.Type, (Pair<RutoASTNode, RutoASTNode>) -> RutoASTNode>(
            RutoToken.Type.MUL to { (left, right) -> RutoASTNode.BinaryOperator.Mul(left, right) },
            RutoToken.Type.DIV to { (left, right) -> RutoASTNode.BinaryOperator.Div(left, right) },
            RutoToken.Type.MOD to { (left, right) -> RutoASTNode.BinaryOperator.Mod(left, right) },
        )

        var node = parsePrimary()
        fun getOper() = peek()?.type?.let { map[it] }
        while (getOper() != null) {
            val oper = getOper()
            advance()
            node = oper!!.invoke(node to parsePrimary())
        }
        return node
    }

    fun parseExpAdd(): RutoASTNode {
        val map = mapOf<RutoToken.Type, (Pair<RutoASTNode, RutoASTNode>) -> RutoASTNode>(
            RutoToken.Type.ADD to { (left, right) -> RutoASTNode.BinaryOperator.Add(left, right) },
            RutoToken.Type.SUB to { (left, right) -> RutoASTNode.BinaryOperator.Sub(left, right) })

        var node = parseExpMul()
        fun getOper() = peek()?.type?.let { map[it] }
        while (getOper() != null) {
            val oper = getOper()
            advance()
            val right = parseExpMul()
            node = oper!!.invoke(node to right)
        }
        return node
    }

    fun parseExp(): RutoASTNode {
        return parseExpAdd()
    }
}