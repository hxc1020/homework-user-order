package com.thoughtworks.userorder.repository.entity

import com.thoughtworks.userorder.common.PaymentChannel
import com.thoughtworks.userorder.dto.PaymentStatus
import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id


@Entity(name = "u_order")
data class Order(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?,
    val address: String,
    val createdAt: LocalDateTime,
    val waitTimeoutAt: LocalDateTime,
    val totalPrice: Double
)

@Entity(name = "u_order_payment")
data class OrderPayment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?,
    val orderId: Long,
    val channel: PaymentChannel,
    val payUrl: String,
    val createdAt: LocalDateTime,
    val paymentExpiredAt: LocalDateTime,
    val totalPrice: Double
)

@Entity(name = "u_order_payment_confirmation")
data class OrderPaymentConfirmation(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?,
    val orderId: Long,
    val confirmAt: LocalDateTime,
    val channel: PaymentChannel,
    val status: PaymentStatus,
    val totalPrice: Double
)

@Entity(name = "u_payment_task")
data class PaymentTask(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?,
    val orderId: Long,
    var status: PaymentTaskStatus,
    val totalPrice: Double,
    val channel: PaymentChannel
)

enum class PaymentTaskStatus {
    WAITING, SUCCESS
}
