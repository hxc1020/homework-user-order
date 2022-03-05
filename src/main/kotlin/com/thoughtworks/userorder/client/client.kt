package com.thoughtworks.userorder.client

import com.thoughtworks.userorder.dto.GetPaymentUrlRequest
import com.thoughtworks.userorder.dto.PaymentInfo
import com.thoughtworks.userorder.dto.PaymentUrlDto
import com.thoughtworks.userorder.exception.ClientException
import com.thoughtworks.userorder.exception.ErrorCode
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import java.net.http.HttpConnectTimeoutException

@FeignClient(name = "payment", url = "http://localhost:1080/")
@Component
interface PaymentClient {

    @PostMapping("/payment-url")
    fun getPaymentUrl(@RequestBody getPaymentUrlRequest: GetPaymentUrlRequest): ResponseEntity<PaymentUrlDto>

    @GetMapping("/payment/{orderId}")
    fun getPaymentInfo(@PathVariable orderId: Long): ResponseEntity<PaymentInfo>
}

fun <T> clientHelper(run: () -> ResponseEntity<T>): T {
    return try {
        val res = run.invoke()
        res.body ?: throw ClientException(ErrorCode.CLIENT_ERROR_RESPONSE)
    } catch (e: HttpConnectTimeoutException) {
        throw ClientException(ErrorCode.CLIENT_CONNECT_TIMEOUT)
    }
}
