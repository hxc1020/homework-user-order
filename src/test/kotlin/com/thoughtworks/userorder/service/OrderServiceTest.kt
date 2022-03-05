package com.thoughtworks.userorder.service

import com.thoughtworks.userorder.client.PaymentClient
import com.thoughtworks.userorder.client.clientHelper
import com.thoughtworks.userorder.common.PaymentChannel
import com.thoughtworks.userorder.dto.PaymentInfo
import com.thoughtworks.userorder.dto.PaymentRequest
import com.thoughtworks.userorder.dto.PaymentStatus
import com.thoughtworks.userorder.dto.PaymentUrlDto
import com.thoughtworks.userorder.exception.BusinessException
import com.thoughtworks.userorder.exception.OrderCannotPayException
import com.thoughtworks.userorder.exception.OrderNotFoundException
import com.thoughtworks.userorder.exception.OrderPaymentNotFoundException
import com.thoughtworks.userorder.repository.OrderPaymentConfirmationRepository
import com.thoughtworks.userorder.repository.OrderPaymentRepository
import com.thoughtworks.userorder.repository.OrderRepository
import com.thoughtworks.userorder.repository.PaymentTaskRepository
import com.thoughtworks.userorder.repository.entity.*
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.http.ResponseEntity
import java.time.LocalDateTime
import javax.persistence.EntityNotFoundException

class OrderServiceTest : FreeSpec({
    val orderRepository = mockk<OrderRepository>()
    val paymentClient = mockk<PaymentClient>()
    val orderPaymentRepository = mockk<OrderPaymentRepository>()
    val orderPaymentConfirmationRepository = mockk<OrderPaymentConfirmationRepository>()
    val paymentTaskRepository = mockk<PaymentTaskRepository>()
    val orderService =
        OrderService(
            orderRepository,
            orderPaymentRepository,
            orderPaymentConfirmationRepository,
            paymentClient,
            paymentTaskRepository
        )

    val paymentRequest = PaymentRequest(PaymentChannel.WECHAT_PAY)

    "when pay" - {
        "should get payment url given order WAIT_FOR_PAYMENT" {
            val now = LocalDateTime.now()
            every { orderRepository.getById(1L) } returns Order(
                1L,
                "address",
                now,
                now.plusMinutes(5),
                10.0
            )
            val mockUrl = "http://mock.pay"
            every { paymentClient.getPaymentUrl(any()) } returns ResponseEntity.ok(PaymentUrlDto(mockUrl))
            every { orderPaymentRepository.findByOrderId(any()) } returns null
            every { orderPaymentRepository.save(any()) } returns mockk()

            val urlDto = orderService.pay(1L, paymentRequest)

            urlDto.payUrl shouldBe mockUrl
        }

        "should throw OrderNotFoundException given unknown order"{
            every { orderRepository.getById(any()) } throws EntityNotFoundException()

            shouldThrow<OrderNotFoundException> {
                orderService.pay(1L, paymentRequest)
            }
        }

        "should throw OrderCannotPayException given PAID order"{
            val now = LocalDateTime.now()
            every { orderRepository.getById(1L) } returns Order(
                1L,
                "address",
                now,
                now.plusMinutes(5),
                10.0
            )
            every { orderPaymentRepository.findByOrderId(any()) } returns mockk()

            shouldThrow<OrderCannotPayException> {
                orderService.pay(1L, paymentRequest)
            }
        }
    }

    "when payConfirm" - {
        "should success given order 1"{
            val now = LocalDateTime.now()
            every { clientHelper { paymentClient.getPaymentInfo(1L) } } returns PaymentInfo(
                1L,
                PaymentStatus.PAID,
                now
            )
            every { orderPaymentRepository.findByOrderId(1L) } returns OrderPayment(
                1L,
                1L,
                PaymentChannel.WECHAT_PAY,
                "http://mock.pay",
                now,
                now.plusMinutes(5),
                10.0
            )
            every { paymentTaskRepository.save(any()) } returns mockk()

            shouldNotThrow<BusinessException> { orderService.payConfirm(1L) }
            verify(exactly = 1) { paymentTaskRepository.save(any()) }
        }

        "should throw OrderPaymentNotFoundException when payConfirm given no orderPayment"{
            every { orderPaymentRepository.findByOrderId(1L) } returns null

            shouldThrow<OrderPaymentNotFoundException> { orderService.payConfirm(1L) }
        }
    }


    "when syncPaymentConfirmation" - {
        "should do nothing given paymentClient return WAIT_FOR_PAYMENT"{
            val now = LocalDateTime.now()
            every { clientHelper { paymentClient.getPaymentInfo(1L) } } returns PaymentInfo(
                1L,
                PaymentStatus.WAIT_FOR_PAYMENT,
                now
            )
            every { orderPaymentConfirmationRepository.save(any()) } returns mockk()
            every { paymentTaskRepository.save(any()) } returns mockk()
            val paymentTask = PaymentTask(1L, 1L, PaymentTaskStatus.WAITING, 10.0, PaymentChannel.ALI_PAY)

            orderService.syncPaymentConfirmation(paymentTask)

            verify(exactly = 0) { orderPaymentConfirmationRepository.save(any()) }
        }

        "should save OrderPaymentConfirmation given paymentClient return PAID"{
            val now = LocalDateTime.now()
            every { clientHelper { paymentClient.getPaymentInfo(1L) } } returns PaymentInfo(
                1L,
                PaymentStatus.PAID,
                now
            )
            every { orderPaymentConfirmationRepository.save(any()) } returns mockk()
            every { paymentTaskRepository.save(any()) } returns mockk()
            val paymentTask = PaymentTask(1L, 1L, PaymentTaskStatus.WAITING, 10.0, PaymentChannel.ALI_PAY)

            orderService.syncPaymentConfirmation(paymentTask)

            verify(exactly = 1) { orderPaymentConfirmationRepository.save(any()) }
        }
    }

    "when getOrderPaymentConfirmation" - {
        "should return PAID given PAID order"{
            every { orderPaymentConfirmationRepository.findByOrderId(any()) } returns OrderPaymentConfirmation(
                id = 1L,
                orderId = 1L,
                confirmAt = LocalDateTime.now(),
                channel = PaymentChannel.WECHAT_PAY,
                status = PaymentStatus.PAID,
                totalPrice = 10.0
            )

            val confirmation = orderService.getPaymentConfirmation(1L)

            confirmation.payStatus shouldBe PaymentStatus.PAID
        }
    }
})
