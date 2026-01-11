package com.rosan.ruto.ruto.script

import java.math.BigDecimal

object RutoInterpreter {
    suspend fun interpret(runtime: RutoRuntime, code: String): RutoASTLiteral {
        val tokens = RutoLexer(code).tokenize()
        val astNodes = RutoParser(tokens).parse()
        var result: RutoASTLiteral = RutoASTLiteral.Void
        for (node in astNodes) {
            result = interpret(runtime, node)
        }
        return result
    }

    suspend fun interpret(runtime: RutoRuntime, node: RutoASTNode): RutoASTLiteral {
        return when (node) {
            is RutoASTNode.BooleanValue -> RutoASTLiteral.BooleanValue(node.value == "true")
            is RutoASTNode.NumberValue -> RutoASTLiteral.NumberValue(BigDecimal(node.value))
            is RutoASTNode.StringValue -> RutoASTLiteral.StringValue(node.value)
            is RutoASTNode.ListValue -> RutoASTLiteral.ListValue(
                node.elements.map { interpret(runtime, it) })

            is RutoASTNode.FunctionCall -> {
                val evaluatedArgs = node.arguments.map { arg ->
                    interpret(runtime, arg)
                }
                runtime.callFunction(node.name, evaluatedArgs)
            }

            is RutoASTNode.BinaryOperator.Add -> {
                val left = interpret(runtime, node.left)
                val right = interpret(runtime, node.right)
                when (left) {
                    is RutoASTLiteral.NumberValue -> RutoASTLiteral.NumberValue(left.number() + right.number())
                    is RutoASTLiteral.StringValue -> RutoASTLiteral.StringValue(left.string() + right.string())
                    else -> throw RutoRuntimeException("$left + $right")
                }
            }

            is RutoASTNode.BinaryOperator.Sub -> {
                val left = interpret(runtime, node.left)
                val right = interpret(runtime, node.right)
                RutoASTLiteral.NumberValue(left.number() - right.number())
            }

            is RutoASTNode.BinaryOperator.Mul -> {
                val left = interpret(runtime, node.left)
                val right = interpret(runtime, node.right)
                RutoASTLiteral.NumberValue(left.number() * right.number())
            }

            is RutoASTNode.BinaryOperator.Div -> {
                val left = interpret(runtime, node.left)
                val right = interpret(runtime, node.right)
                RutoASTLiteral.NumberValue(left.number() / right.number())
            }

            is RutoASTNode.BinaryOperator.Mod -> {
                val left = interpret(runtime, node.left)
                val right = interpret(runtime, node.right)
                RutoASTLiteral.NumberValue(left.number() % right.number())
            }
        }
    }
}