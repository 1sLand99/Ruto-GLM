package com.rosan.ruto.ruto.script

open class RutoRuntime {
    private val functions = mutableMapOf<String, RutoFunction>()
    fun registerFunction(name: String, action: RutoFunction) {
        functions[name] = action
    }

    fun registerFunction(
        name: String, action: RutoFunction.Scope.() -> Any
    ) {
        registerFunction(name, RutoFunction {
            RutoASTLiteral.wrap(action())
        })
    }

    fun unregisterFunction(vararg name: String) {
        name.forEach { functions.remove(it) }
    }

    fun callFunction(name: String, arguments: List<RutoASTLiteral>) =
        functions[name]?.action?.invoke(RutoFunction.Scope(arguments))
            ?: throw RutoRuntimeException("Function $name not found")

    fun callFunction(name: String, vararg arguments: Any) {
        callFunction(name, arguments.map { RutoASTLiteral.wrap(it) })
    }

    fun exec(code: String): Any {
        return RutoInterpreter.interpret(this, code).unwrap()
    }
}