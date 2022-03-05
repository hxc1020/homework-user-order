package com.thoughtworks.userorder.timer

import com.thoughtworks.userorder.exception.OrderCannotPayException
import com.thoughtworks.userorder.repository.PaymentTaskRepository
import com.thoughtworks.userorder.repository.entity.PaymentTaskStatus
import com.thoughtworks.userorder.service.OrderService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class SyncPaymentConfirmationTimer(
    val orderService: OrderService,
    val paymentTaskRepository: PaymentTaskRepository,
) {
    private val log: Logger = LoggerFactory.getLogger(SyncPaymentConfirmationTimer::class.java)

    @Scheduled(cron = "0/3 * * * * ?")
    fun execute() {
        paymentTaskRepository.findByStatus(PaymentTaskStatus.WAITING)
            .forEach {
                try {
                    orderService.syncPaymentConfirmation(it)
                } catch (e: OrderCannotPayException) {
                    log.error("order cannot sync payment confirmation, orderId:{}", it.orderId)
                }
            }
    }
}
