package com.thoughtworks.userorder.dto

import com.thoughtworks.userorder.common.LocalDateTimeSerializer
import com.thoughtworks.userorder.common.PaymentChannel
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

data class PaymentRequest(val channel: PaymentChannel)

data class GetPaymentUrlRequest(val orderId: Long, val channel: PaymentChannel, val amount: Double)

data class PaymentUrlDto(val payUrl: String)

@Serializable
data class PaymentInfo(
    val orderId: Long,
    val status: PaymentStatus,
    @Serializable(with = LocalDateTimeSerializer::class)
    val payAt: LocalDateTime
)

data class OrderPaymentConfirmationDto(
    val orderId: Long,
    val confirmAt: LocalDateTime,
    val channel: PaymentChannel,
    val payStatus: PaymentStatus,
    val totalPrice: Double
)

enum class PaymentStatus {
    WAIT_FOR_PAYMENT, PAID, FAILED
}
