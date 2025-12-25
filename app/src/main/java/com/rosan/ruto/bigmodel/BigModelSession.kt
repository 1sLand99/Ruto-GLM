package com.rosan.ruto.bigmodel

import android.util.Log
import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.Content
import dev.langchain4j.data.message.ImageContent
import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.data.message.TextContent
import dev.langchain4j.data.message.UserMessage
import dev.langchain4j.kotlin.model.chat.StreamingChatModelReply
import dev.langchain4j.kotlin.model.chat.chatFlow
import dev.langchain4j.model.chat.StreamingChatModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach

class BigModelSession(
    private val model: StreamingChatModel, private val prompt: String
) {
    private val memory = mutableListOf<ChatMessage>(SystemMessage.from(prompt))

    fun request(
        text: String? = null, image: ImageContent? = null
    ): Flow<StreamingChatModelReply> {
        val assist = StringBuilder()
        return model.chatFlow {
            messages(memory)

            val contents = mutableListOf<Content>()
            text?.let { contents.add(TextContent.from(it)) }
            image?.let { contents.add(it) }

            message(UserMessage.from(contents))

            Log.e("r0s", memory.joinToString(","))
        }.onEach { reply ->
            if (reply !is StreamingChatModelReply.PartialResponse) return@onEach
            assist.append(reply.partialResponse)
        }.onCompletion {
            Log.e("r0s", "completion")
            text?.let { memory.add(UserMessage.from(text)) }
            memory.add(AiMessage.from(assist.toString()))
        }
    }
}