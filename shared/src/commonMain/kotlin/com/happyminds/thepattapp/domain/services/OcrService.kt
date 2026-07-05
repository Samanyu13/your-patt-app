package com.happyminds.thepattapp.domain.services

import kotlinx.serialization.Serializable

@Serializable
data class ReceiptItem(
    val description: String,
    val amount: Double
)

@Serializable
data class ReceiptResult(
    val items: List<ReceiptItem>,
    val totalAmount: Double
)

interface OcrService {
    suspend fun scanReceipt(imageData: ByteArray): ReceiptResult
}

class MockOcrService : OcrService {
    override suspend fun scanReceipt(imageData: ByteArray): ReceiptResult {
        // Mocking OCR results
        return ReceiptResult(
            items = listOf(
                ReceiptItem("Pizza", 25.0),
                ReceiptItem("Drinks", 12.5),
                ReceiptItem("Salad", 8.0)
            ),
            totalAmount = 45.5
        )
    }
}
