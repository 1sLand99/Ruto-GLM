package com.rosan.installer.ext.util

import android.content.Context
import java.io.File
import java.io.InputStream
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import org.koin.core.context.GlobalContext.get as getKoin

// 从字节流创建缓存文件
fun InputStream.temp(tag: String): File = use {
    // 确保临时目录存在
    val tempDir = File(getKoin().get<Context>().cacheDir, tag).apply {
        if (!exists()) mkdirs()
    }

    // 生成唯一临时文件名
    @OptIn(ExperimentalUuidApi::class) val tempFileName = Uuid.random().toHexString()
    val tempFile = File.createTempFile(tempFileName, null, tempDir)

    tempFile.outputStream().use { output ->
        copyTo(output)
    }
    tempFile
}