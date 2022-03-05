package com.thoughtworks.userorder.repository

import com.thoughtworks.userorder.repository.entity.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderRepository : JpaRepository<Order, Long>

@Repository
interface OrderPaymentRepository : JpaRepository<OrderPayment, Long> {
    fun findByOrderId(orderId: Long): OrderPayment?
}

@Repository
interface OrderPaymentConfirmationRepository : JpaRepository<OrderPaymentConfirmation, Long>{
    fun findByOrderId(orderId: Long): OrderPaymentConfirmation?
}

@Repository
interface PaymentTaskRepository : JpaRepository<PaymentTask, Long> {
    fun findByStatus(paymentTaskStatus: PaymentTaskStatus): List<PaymentTask>
}
