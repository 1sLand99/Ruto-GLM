package com.rosan.ruto.retrofit

import dev.langchain4j.http.client.HttpClientBuilder
import dev.langchain4j.http.client.HttpClientBuilderFactory

class RetrofitHttpClientBuilderFactory : HttpClientBuilderFactory {
    override fun create(): HttpClientBuilder = RetrofitHttpClient.builder()
}