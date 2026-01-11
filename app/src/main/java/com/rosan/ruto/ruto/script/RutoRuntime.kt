package com.rosan.ruto.ruto.script

open class RutoRuntime {
    private val functions = mutableMapOf<String, RutoFunction>()
    fun registerFunction(name: String, action: RutoFunction) {
        functions[name] = action
    }

    fun registerFunction(
        name: String, action: suspend RutoFunction.Scope.() -> Any
    ) {
        registerFunction(name, RutoFunction {
            RutoASTLiteral.wrap(action())
        })
    }

    fun unregisterFunction(vararg name: String) {
        name.forEach { functions.remove(it) }
    }

    suspend fun callFunction(name: String, arguments: List<RutoASTLiteral>) =
        functions[name]?.action?.invoke(RutoFunction.Scope(arguments))
            ?: throw RutoRuntimeException("Function $name not found")

    suspend fun callFunction(name: String, vararg arguments: Any) {
        callFunction(name, arguments.map { RutoASTLiteral.wrap(it) })
    }

    suspend fun exec(code: String): Any {
        return RutoInterpreter.interpret(this, code).unwrap()
    }
}