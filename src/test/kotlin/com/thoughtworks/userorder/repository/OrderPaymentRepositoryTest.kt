package com.thoughtworks.userorder.repository

import com.thoughtworks.userorder.common.PaymentChannel
import com.thoughtworks.userorder.repository.entity.OrderPayment
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.StringSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.extensions.testcontainers.JdbcTestContainerExtension
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import java.time.LocalDateTime

@SpringBootTest
@ContextConfiguration(initializers = [Initializer::class])
class OrderPaymentRepositoryTest(val orderPaymentRepository: OrderPaymentRepository) : StringSpec({

    val ds = install(JdbcTestContainerExtension(mysql))

    "should save OrderPayment when call save given OrderPayment" {
        val order = withContext(Dispatchers.IO) {
            orderPaymentRepository.save(
                OrderPayment(
                    null,
                    1L,
                    PaymentChannel.WECHAT_PAY,
                    "http://mock.pay",
                    LocalDateTime.now(),
                    LocalDateTime.now().plusMinutes(5),
                    10.0
                )
            )
        }

        order.id shouldNotBe null
        order.orderId shouldBe 1L
    }

}) {
    override fun extensions() = listOf(SpringExtension)
}

