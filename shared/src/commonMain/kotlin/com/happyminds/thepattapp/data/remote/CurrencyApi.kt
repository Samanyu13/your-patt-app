package com.happyminds.thepattapp.data.remote

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class ExchangeRateResponse(
    val base_code: String,
    val conversion_rates: Map<String, Double>
)

class CurrencyApi(private val client: HttpClient) {
    suspend fun getLatestRates(baseCurrency: String): ExchangeRateResponse {
        // Using a public API for demonstration. In production, use an API key.
        return client.get("https://open.er-api.com/v6/latest/$baseCurrency").body()
    }
}

fun createHttpClient() = HttpClient {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
        })
    }
}
