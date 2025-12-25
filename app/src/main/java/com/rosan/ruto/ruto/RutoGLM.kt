package com.rosan.ruto.ruto

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Base64
import android.util.Log
import android.view.Display
import com.rosan.ruto.autoglm.script.AutoGLMRuntime
import com.rosan.ruto.bigmodel.BigModel
import com.rosan.ruto.bigmodel.BigModelSession
import com.rosan.ruto.device.repo.DeviceRepo
import com.rosan.ruto.retrofit.RetrofitHttpClientBuilderFactory
import dev.langchain4j.data.message.ImageContent
import dev.langchain4j.kotlin.model.chat.StreamingChatModelReply
import dev.langchain4j.model.chat.StreamingChatModel
import dev.langchain4j.model.openai.OpenAiStreamingChatModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import java.io.ByteArrayOutputStream
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class RutoGLM(
    private val bigModelSession: BigModelSession,
    private val runtime: AutoGLMRuntime,
    private val device: DeviceRepo
) {
    var displayId: Int = Display.DEFAULT_DISPLAY

    var quality: Int = 0

    var delayDuration: Duration = 0.5.seconds

    constructor(
        bigModel: BigModel, prompt: String, runtime: AutoGLMRuntime, device: DeviceRepo
    ) : this(
        bigModel.openSession(prompt), runtime, device
    )

    constructor(
        model: StreamingChatModel,
        prompt: String,
        runtime: AutoGLMRuntime,
        device: DeviceRepo
    ) : this(BigModel(model), prompt, runtime, device)

    constructor(
        modelUrl: String,
        modelName: String,
        apiKey: String,
        prompt: String,
        runtime: AutoGLMRuntime,
        device: DeviceRepo
    ) : this(
        OpenAiStreamingChatModel.builder()
            .baseUrl(modelUrl)
            .modelName(modelName)
            .apiKey(apiKey)
            .temperature(0.1)
            .maxTokens(3000)
            .topP(0.85)
            .frequencyPenalty(0.2)
            .httpClientBuilder(RetrofitHttpClientBuilderFactory().create())
            .build(), prompt, runtime, device
    )

    private fun capture(): ImageContent {
        val bitmap = device.displayManager.capture(displayId).bitmap

        val bytes = try {
            val stream = ByteArrayOutputStream()
            val format =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) Bitmap.CompressFormat.WEBP_LOSSY
                else Bitmap.CompressFormat.WEBP
            bitmap.compress(format, quality, stream)
            stream.toByteArray()
        } finally {
            bitmap.recycle()
        }

        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)

        val mimeType = options.outMimeType
        val base64String = Base64.encodeToString(bytes, Base64.NO_WRAP)

        return ImageContent(base64String, mimeType)
    }

    suspend fun ruto(
        text: String? = null,
        onStreaming: (think: String) -> Unit = {},
        onComplete: (think: String) -> Unit = {}
    ): String {
        Log.e("r0s", "task $text")
        val image = capture()
        Log.e("r0s", "capture $image")
        val glmStreamParser = GLMStreamParser()
        Log.e("r0s", "request $image")
        bigModelSession.request(text, image).onEach { reply ->
            if (reply !is StreamingChatModelReply.PartialResponse) return@onEach
            Log.e("r0s", "reply ${reply.partialResponse}")
            glmStreamParser.tokenize(reply.partialResponse)
            glmStreamParser.parse(onThink = onStreaming)
        }.collect()

        Log.e("r0s", "collected")

        var result = ""
        glmStreamParser.parse(
            onThink = onComplete, onAction = {
                Log.e("r0s", "action $it")
                try {
                    runtime.exec(it)
                } catch (e: RutoFinishException) {
                    result = e.msg
                    return@parse
                }
                delay(delayDuration)
                ruto(text = "指令执行成功", onStreaming = onStreaming, onComplete = onComplete)
            })
        return result
    }
}