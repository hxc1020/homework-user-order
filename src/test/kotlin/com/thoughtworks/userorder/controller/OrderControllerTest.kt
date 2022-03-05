package com.thoughtworks.userorder.controller

import com.thoughtworks.userorder.UserOrderApplication
import com.thoughtworks.userorder.common.PaymentChannel
import com.thoughtworks.userorder.dto.OrderPaymentConfirmationDto
import com.thoughtworks.userorder.dto.PaymentStatus
import com.thoughtworks.userorder.dto.PaymentUrlDto
import com.thoughtworks.userorder.exception.ExceptionHandler
import com.thoughtworks.userorder.exception.OrderNotFoundException
import com.thoughtworks.userorder.exception.OrderPaymentNotFoundException
import com.thoughtworks.userorder.service.OrderService
import io.kotest.core.spec.style.StringSpec
import io.kotest.extensions.spring.SpringExtension
import io.mockk.every
import io.mockk.mockk
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.time.LocalDateTime

@SpringBootTest(classes = [UserOrderApplication::class, ExceptionHandler::class])
class OrderControllerTest : StringSpec({
    val orderService = mockk<OrderService>()
    val mvc: MockMvc = MockMvcBuilders.standaloneSetup(OrderController(orderService)).build()
    val orderId = 1L

    "should return success when call pay"{
        every { orderService.pay(any(), any()) } returns PaymentUrlDto("http://mock.pay")
        mvc.post("/orders/{oid}/payment", orderId) {
            contentType = MediaType.APPLICATION_JSON
            content = """
                { "channel": "WECHAT_PAY"}
            """.trimIndent()
        }.andExpect {
            status { is2xxSuccessful() }
        }
    }

    "should return 404 when call pay given order not found"{
        every { orderService.pay(any(), any()) } throws OrderNotFoundException()
        mvc.post("/orders/{oid}/payment", orderId) {
            contentType = MediaType.APPLICATION_JSON
            content = """
                { "channel": "WECHAT_PAY"}
            """.trimIndent()
        }.andExpect {
            status { isNotFound() }
            content {
                jsonPath("message") {
                    value("订单不存在")
                }
            }
        }
    }

    "should return success when call payConfirm given orderPayment"{
        every { orderService.payConfirm(1L) } returns Unit

        mvc.post("/orders/{oid}/payment/confirmation", orderId) {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { is2xxSuccessful() }
        }
    }

    "should return 404 when call payConfirm given no orderPayment"{
        every { orderService.payConfirm(1L) } throws OrderPaymentNotFoundException()

        mvc.post("/orders/{oid}/payment/confirmation", orderId) {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
            content {
                jsonPath("message") {
                    value("订单未支付")
                }
            }
        }
    }

    "should return success when all /orders/1/payment/confirmation given paid order"{
        every { orderService.getPaymentConfirmation(1L) } returns OrderPaymentConfirmationDto(
            orderId = 1,
            confirmAt = LocalDateTime.now(),
            channel = PaymentChannel.WECHAT_PAY,
            payStatus = PaymentStatus.PAID,
            totalPrice = 10.0
        )

        mvc.get("/orders/{oid}/payment/confirmation", orderId) {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { is2xxSuccessful() }
            content {
                jsonPath("status") {
                    value("PAID")
                }
            }
        }
    }

    "should return success when all /orders/1/payment/confirmation given failed order"{
        every { orderService.getPaymentConfirmation(1L) } returns OrderPaymentConfirmationDto(
            orderId = 1,
            confirmAt = LocalDateTime.now(),
            channel = PaymentChannel.WECHAT_PAY,
            payStatus = PaymentStatus.FAILED,
            totalPrice = 10.0
        )

        mvc.get("/orders/{oid}/payment/confirmation", orderId) {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { is2xxSuccessful() }
            content {
                jsonPath("status") {
                    value("FAILED")
                }
            }
        }
    }
}) {
    override fun extensions() = listOf(SpringExtension)
}
