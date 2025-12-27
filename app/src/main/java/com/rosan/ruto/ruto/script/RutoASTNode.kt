package com.rosan.ruto.ruto.script

sealed class RutoASTNode {
    data class BooleanValue(val value: String) : RutoASTNode()
    data class NumberValue(val value: String) : RutoASTNode()
    data class StringValue(val value: String) : RutoASTNode()
    data class ListValue(val elements: List<RutoASTNode>) : RutoASTNode()

    sealed class BinaryOperator(open val left: RutoASTNode, open val right: RutoASTNode) :
        RutoASTNode() {
        data class Add(override val left: RutoASTNode, override val right: RutoASTNode) :
            BinaryOperator(left, right)

        data class Sub(override val left: RutoASTNode, override val right: RutoASTNode) :
            BinaryOperator(left, right)

        data class Mul(override val left: RutoASTNode, override val right: RutoASTNode) :
            BinaryOperator(left, right)

        data class Div(override val left: RutoASTNode, override val right: RutoASTNode) :
            BinaryOperator(left, right)

        data class Mod(override val left: RutoASTNode, override val right: RutoASTNode) :
            BinaryOperator(left, right)
    }

    data class FunctionCall(
        val name: String,
        val arguments: List<RutoASTNode> = emptyList()
    ) : RutoASTNode()
}