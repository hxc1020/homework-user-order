package com.thoughtworks.userorder.repository

import io.kotest.core.extensions.install
import io.kotest.core.spec.style.StringSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.extensions.testcontainers.JdbcTestContainerExtension
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration


@SpringBootTest
@ContextConfiguration(initializers = [Initializer::class])
class OrderRepositoryTest(val orderRepository: OrderRepository) : StringSpec({

    val ds = install(JdbcTestContainerExtension(mysql))

    "should get Order when call getById given a Order" {
        performQuery(
            ds, """
            insert into `u_order`(id, status, created_at, payment_expired_at, wait_timeout_at, total_price)
            VALUES (1, 1, now(), now(), now(), 10.0)
        """.trimIndent()
        )
        val order = withContext(Dispatchers.IO) {
            orderRepository.getById(1L)
        }

        order.id shouldBe 1L
    }

}) {
    override fun extensions() = listOf(SpringExtension)
}

