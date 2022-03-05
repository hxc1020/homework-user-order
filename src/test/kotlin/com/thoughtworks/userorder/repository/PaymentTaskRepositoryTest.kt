package com.thoughtworks.userorder.repository

import com.thoughtworks.userorder.common.PaymentChannel
import com.thoughtworks.userorder.repository.entity.PaymentTaskStatus
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.StringSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.extensions.testcontainers.JdbcTestContainerExtension
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration

@SpringBootTest
@ContextConfiguration(initializers = [Initializer::class])
class PaymentTaskRepositoryTest(val paymentTaskRepository: PaymentTaskRepository) : StringSpec({

    val ds = install(JdbcTestContainerExtension(mysql))

    "should get paymentTask when call findByStatus given one task" {
        performQuery(
            ds, """
            insert into u_payment_task (id, order_id, status, total_price, channel) values (1, 1, 0, 10.0, 0)
        """.trimIndent()
        )
        val task = withContext(Dispatchers.IO) {
            paymentTaskRepository.findByStatus(PaymentTaskStatus.WAITING)
        }

        task shouldHaveSize 1
        task[0].status shouldBe PaymentTaskStatus.WAITING
        task[0].channel shouldBe PaymentChannel.WECHAT_PAY
    }

}) {
    override fun extensions() = listOf(SpringExtension)
}
