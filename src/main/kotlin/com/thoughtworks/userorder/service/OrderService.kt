package com.thoughtworks.userorder.service

import com.thoughtworks.userorder.client.PaymentClient
import com.thoughtworks.userorder.client.clientHelper
import com.thoughtworks.userorder.dto.*
import com.thoughtworks.userorder.exception.*
import com.thoughtworks.userorder.repository.OrderPaymentConfirmationRepository
import com.thoughtworks.userorder.repository.OrderPaymentRepository
import com.thoughtworks.userorder.repository.OrderRepository
import com.thoughtworks.userorder.repository.PaymentTaskRepository
import com.thoughtworks.userorder.repository.entity.OrderPayment
import com.thoughtworks.userorder.repository.entity.OrderPaymentConfirmation
import com.thoughtworks.userorder.repository.entity.PaymentTask
import com.thoughtworks.userorder.repository.entity.PaymentTaskStatus
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import javax.persistence.EntityNotFoundException

@Service
class OrderService(
    val orderRepository: OrderRepository,
    val orderPaymentRepository: OrderPaymentRepository,
    val orderPaymentConfirmationRepository: OrderPaymentConfirmationRepository,
    val paymentClient: PaymentClient,
    val paymentTaskRepository: PaymentTaskRepository
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    fun pay(orderId: Long, paymentRequest: PaymentRequest): PaymentUrlDto {
        val order = try {
            orderRepository.getById(orderId)
        } catch (e: EntityNotFoundException) {
            throw OrderNotFoundException()
        }
        if (orderPaymentRepository.findByOrderId(orderId) != null) {
            throw OrderCannotPayException()
        }
        val urlDto = clientHelper {
            paymentClient.getPaymentUrl(
                GetPaymentUrlRequest(orderId, paymentRequest.channel, order.totalPrice)
            )
        }
        OrderPayment(
            id = null,
            orderId = orderId,
            channel = paymentRequest.channel,
            payUrl = urlDto.payUrl,
            createdAt = LocalDateTime.now(),
            paymentExpiredAt = LocalDateTime.now().plusMinutes(5),
            totalPrice = order.totalPrice
        ).let { orderPaymentRepository.save(it) }

        return urlDto
    }

    fun payConfirm(oid: Long) {
        val orderPayment = orderPaymentRepository.findByOrderId(oid) ?: throw OrderPaymentNotFoundException()

        PaymentTask(
            id = null,
            orderId = oid,
            status = PaymentTaskStatus.WAITING,
            channel = orderPayment.channel,
            totalPrice = orderPayment.totalPrice
        ).let { paymentTaskRepository.save(it) }
    }

    fun syncPaymentConfirmation(paymentTask: PaymentTask) {
        val paymentInfo: PaymentInfo
        try {
            paymentInfo = clientHelper { paymentClient.getPaymentInfo(paymentTask.orderId) }
        } catch (e: ClientException) {
            log.error("call payment client error, cause {}, e: ", e.errorCode.message, e)
            return
        }
        when (paymentInfo.status) {
            PaymentStatus.PAID, PaymentStatus.FAILED -> {
                orderPaymentConfirmationRepository.save(
                    OrderPaymentConfirmation(
                        id = null,
                        orderId = paymentInfo.orderId,
                        confirmAt = paymentInfo.payAt,
                        channel = paymentTask.channel,
                        status = paymentInfo.status,
                        totalPrice = paymentTask.totalPrice
                    )
                )
                paymentTask.status = PaymentTaskStatus.SUCCESS
                paymentTaskRepository.save(paymentTask)
            }
            PaymentStatus.WAIT_FOR_PAYMENT -> Unit
        }
    }

    fun getPaymentConfirmation(oid: Long): OrderPaymentConfirmationDto {
        val confirmation = orderPaymentConfirmationRepository.findByOrderId(oid)
            ?: throw OrderPaymentConfirmationNotFoundException()
        return OrderPaymentConfirmationDto(
            orderId = confirmation.orderId,
            confirmAt = confirmation.confirmAt,
            channel = confirmation.channel,
            payStatus = confirmation.status,
            totalPrice = confirmation.totalPrice
        )
    }
}
