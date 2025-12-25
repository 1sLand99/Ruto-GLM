package com.rosan.ruto.autoglm.script

sealed class RutoASTNode {
    data class NumberValue(val value: Int) : RutoASTNode()
    data class StringValue(val value: String) : RutoASTNode()
    data class ListValue(val elements: List<RutoASTNode>) : RutoASTNode()

    data class FunctionCall(
        val name: String,
        val arguments: Map<String, RutoASTNode> = emptyMap()
    ) : RutoASTNode()
}