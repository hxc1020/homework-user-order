package com.thoughtworks.userorder.controller

import com.thoughtworks.userorder.dto.OrderPaymentConfirmationDto
import com.thoughtworks.userorder.dto.PaymentRequest
import com.thoughtworks.userorder.dto.PaymentUrlDto
import com.thoughtworks.userorder.service.OrderService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/orders")
class OrderController(val orderService: OrderService) {
    @PostMapping("/{oid}/payment")
    fun payment(
        @PathVariable oid: Long,
        @RequestBody paymentRequest: PaymentRequest
    ): ResponseEntity<PaymentUrlDto> {
        return orderService.pay(oid, paymentRequest).let { ResponseEntity.ok(it) }
    }

    @PostMapping("/{oid}/payment/confirmation")
    fun paymentConfirm(
        @PathVariable oid: Long
    ): ResponseEntity<Void> {
        orderService.payConfirm(oid)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/{oid}/payment/confirmation")
    fun getPaymentConfirmation(
        @PathVariable oid: Long
    ): ResponseEntity<OrderPaymentConfirmationDto> {
        return ResponseEntity.ok(orderService.getPaymentConfirmation(oid))
    }
}
