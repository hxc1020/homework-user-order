package com.thoughtworks.userorder.exception

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(BusinessException::class, SystemException::class)
    fun handleOrderCannotPay(e: BusinessException): ResponseEntity<ErrorEntity> =
        ResponseEntity.status(e.errorCode.code).body(ErrorEntity(e.errorCode.message))
}

data class ErrorEntity(val message: String)
