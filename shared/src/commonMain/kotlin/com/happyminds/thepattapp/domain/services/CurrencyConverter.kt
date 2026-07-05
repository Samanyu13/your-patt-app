package com.happyminds.thepattapp.domain.services

import com.happyminds.thepattapp.data.remote.CurrencyApi

class CurrencyConverter(private val api: CurrencyApi) {
    private var cachedRates: Map<String, Double>? = null
    private var cachedBase: String? = null

    suspend fun convert(amount: Double, from: String, to: String): Double {
        if (from == to) return amount

        val rates = getRates(from)
        val rate = rates[to] ?: return amount // Fallback to original if rate not found
        
        return amount * rate
    }

    private suspend fun getRates(base: String): Map<String, Double> {
        if (cachedBase == base && cachedRates != null) {
            return cachedRates!!
        }

        return try {
            val response = api.getLatestRates(base)
            cachedBase = base
            cachedRates = response.conversion_rates
            response.conversion_rates
        } catch (e: Exception) {
            emptyMap()
        }
    }
}
