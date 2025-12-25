package com.rosan.ruto.autoglm.script

typealias AutoGLMRuntimeFunction = (Map<String, Any>) -> Any

open class AutoGLMRuntime {
    private val functions = mutableMapOf<String, AutoGLMRuntimeFunction>()

    fun defFunction(name: String, action: AutoGLMRuntimeFunction) {
        functions[name] = action
    }

    fun undefFunction(name: String) {
        functions.remove(name)
    }

    fun callFunction(name: String, arguments: Map<String, Any>) =
        functions[name]?.invoke(arguments)
            ?: throw RutoRuntimeException("Function $name not found")

    fun exec(code: String): Any {
        val tokens = RutoLexer(code).tokenize()
        val astNodes = RutoParser(tokens)
            .parse()
        val interpreter = RutoInterpreter(this)
        var result: Any = Unit
        for (node in astNodes) {
            result = interpreter.interpret(node)
        }
        return result
    }
}