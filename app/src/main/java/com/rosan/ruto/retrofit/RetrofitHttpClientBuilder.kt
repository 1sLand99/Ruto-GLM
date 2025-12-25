package com.rosan.ruto.retrofit

import dev.langchain4j.http.client.HttpClient
import dev.langchain4j.http.client.HttpClientBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.time.Duration
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class RetrofitHttpClientBuilder : HttpClientBuilder {
    private var connectTimeout: Duration = Duration.ofSeconds(30)
    private var readTimeout: Duration = Duration.ofSeconds(30)

    override fun connectTimeout(): Duration = connectTimeout

    override fun connectTimeout(timeout: Duration): HttpClientBuilder {
        connectTimeout = timeout
        return this
    }

    override fun readTimeout(): Duration = readTimeout

    override fun readTimeout(timeout: Duration): HttpClientBuilder {
        readTimeout = timeout
        return this
    }

    override fun build(): HttpClient {
        val okHttpClient =
            OkHttpClient.Builder().connectTimeout(connectTimeout.toMillis(), TimeUnit.MILLISECONDS)
                .readTimeout(readTimeout.toMillis(), TimeUnit.MILLISECONDS).build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://localhost/") // 占位地址
            .client(okHttpClient)
            .callbackExecutor(Executors.newFixedThreadPool(4))
            .build()

        return RetrofitHttpClient(retrofit.create(RetrofitHttpClient.GenericApi::class.java))
    }
}