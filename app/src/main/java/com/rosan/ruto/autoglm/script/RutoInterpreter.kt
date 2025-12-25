package com.rosan.ruto.autoglm.script

class RutoInterpreter(private val runtime: AutoGLMRuntime) {
    fun interpret(node: RutoASTNode): Any {
        return when (node) {
            is RutoASTNode.NumberValue -> node.value
            is RutoASTNode.StringValue -> node.value
            is RutoASTNode.ListValue -> node.elements.map { interpret(it) }
            is RutoASTNode.FunctionCall -> {
                val evaluatedArgs = node.arguments.mapValues { (_, value) ->
                    interpret(value)
                }

                runtime.callFunction(node.name, evaluatedArgs)
            }
        }
    }
}