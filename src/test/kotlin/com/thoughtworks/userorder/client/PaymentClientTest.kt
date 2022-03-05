package com.thoughtworks.userorder.client

import com.thoughtworks.userorder.UserOrderApplication
import com.thoughtworks.userorder.common.PaymentChannel
import com.thoughtworks.userorder.dto.GetPaymentUrlRequest
import com.thoughtworks.userorder.dto.PaymentInfo
import com.thoughtworks.userorder.dto.PaymentStatus
import io.kotest.core.spec.style.StringSpec
import io.kotest.extensions.mockserver.MockServerListener
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.mockserver.client.MockServerClient
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import org.mockserver.model.JsonBody
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime

@SpringBootTest(classes = [UserOrderApplication::class])
class PaymentClientTest(val paymentClient: PaymentClient) : StringSpec({
    listener(MockServerListener(1080))

    "should return success when call /payment-url given order 1"{
        MockServerClient("localhost", 1080).`when`(
            HttpRequest.request().withMethod("POST").withPath("/payment-url")
        ).respond(
            HttpResponse.response().withStatusCode(200).withBody(
                JsonBody(
                    """
                    {"payUrl": "http://mock.pay"}
                """.trimIndent()
                )
            )
        )
        val result = withContext(Dispatchers.IO) {
            clientHelper {
                paymentClient.getPaymentUrl(
                    GetPaymentUrlRequest(
                        orderId = 1L, channel = PaymentChannel.WECHAT_PAY, amount = 10.0
                    )
                )
            }
        }
        result.payUrl shouldBe "http://mock.pay"
    }

    "should return success when call /payment/{oid} given order 1"{
        MockServerClient("localhost", 1080).`when`(
            HttpRequest.request().withMethod("GET").withPath("/payment/{oid}").withPathParameter("oid", "1")
        ).respond(
            HttpResponse.response().withStatusCode(200).withBody(
                JsonBody(Json.encodeToString(PaymentInfo(1L, PaymentStatus.PAID, LocalDateTime.now())))
            )
        )
        val result = withContext(Dispatchers.IO) {
            clientHelper {
                paymentClient.getPaymentInfo(1L)
            }
        }
        result.status shouldBe PaymentStatus.PAID
    }
}) {
    override fun extensions() = listOf(SpringExtension)
}
