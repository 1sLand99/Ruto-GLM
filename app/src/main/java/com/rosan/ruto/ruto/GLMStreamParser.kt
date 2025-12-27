package com.rosan.ruto.ruto

import com.rosan.ruto.ruto.script.RutoLexerException

class GLMStreamParser {
    private enum class ParseState(val tag: String) {
        THINK_START("<think>"),
        THINK_END("</think>"),
        ACTION_START("<action>"),
        ACTION_END("</action>")
    }

    // 存储所有的文本
    private val builder = StringBuilder()

    private var pos = 0

    private val list = mutableListOf<Pair<ParseState?, IntRange>>()

    suspend fun tokenize(chunk: String) {
        builder.append(chunk)

        val states = ParseState.entries

        while (pos < builder.length) {
            // 检查匹配标签
            val match = states.find {
                builder.startsWith(it.tag, pos)
            }

            if (match != null) {
                list.add(Pair(match, pos until (pos + match.tag.length)))
                pos += match.tag.length
                continue
            }

            // 不完整匹配，可能是还没传完
            val potentialMatch = states.find {
                // 如果剩余长度大于标志，就不可能是还没传完
                if (builder.length - pos > it.tag.length) return@find false

                val remaining = builder.substring(pos)
                return@find it.tag.startsWith(remaining)
            }

            if (potentialMatch != null) {
                // 如果是潜在的半截标签，且后面没有更多字符了。
                // 我们就跳出循环，等待下一个 chunk
                break
            }

            normal()
        }
    }

    suspend fun parse(
        onThink: (suspend (String) -> Unit)? = null,
        onAction: (suspend (String) -> Unit)? = null
    ) {
        suspend fun onInnerAction(action: String) {
            try {
                onAction?.invoke(action)
            } catch (e: RutoLexerException) {
                if (!action.startsWith("finish(")) return
                onAction?.invoke("finish()")
            }
        }

        var withAction = false
        var think = ""
        var curState: ParseState? = null
        for ((state, range) in list) {
            when (state) {
                ParseState.ACTION_START -> curState = state
                ParseState.ACTION_END -> curState = null
                ParseState.THINK_START -> curState = state
                ParseState.THINK_END -> curState = null
                null -> {
                    if (curState == ParseState.ACTION_START) {
                        withAction = true
                        onInnerAction(builder.substring(range))
                    } else {
                        think += builder.substring(range)
                    }
                }
            }
        }
        if (withAction || onAction == null) {
            onThink?.invoke(think)
            return
        }
        val lines = think.lines()
        if (lines.isNotEmpty()) onInnerAction(think.lines().last())
        else onThink?.invoke(lines.subList(0, lines.size - 1).joinToString("\n"))
    }

    private fun normal() {
        // 属于普通文本
        val last = list.lastOrNull()
        if (last == null || last.first != null) {
            list.add(null to (pos until pos + 1))
        } else {
            list[list.lastIndex] = null to (last.second.first until pos + 1)
        }
        pos++
    }
}