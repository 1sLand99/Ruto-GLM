package com.rosan.ruto.bigmodel

import dev.langchain4j.model.chat.StreamingChatModel

class BigModel(private val model: StreamingChatModel) {
    constructor(action: () -> StreamingChatModel) : this(action.invoke())

    fun openSession(prompt: String): BigModelSession = BigModelSession(model, prompt)
}