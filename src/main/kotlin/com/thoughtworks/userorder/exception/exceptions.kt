package com.thoughtworks.userorder.exception

import org.springframework.http.HttpStatus

open class BusinessException(val errorCode: ErrorCode) : RuntimeException(errorCode.message)

open class SystemException(errorCode: ErrorCode) : RuntimeException(errorCode.message)

open class ClientException(val errorCode: ErrorCode) : SystemException(errorCode)

class OrderNotFoundException : BusinessException(ErrorCode.ORDER_NOT_FOUND)

class OrderPaymentNotFoundException : BusinessException(ErrorCode.ORDER_PAYMENT_NOT_FOUND)

class OrderPaymentConfirmationNotFoundException : BusinessException(ErrorCode.ORDER_PAYMENT_CONFIRMATION_NOT_FOUND)

class OrderCannotPayException : BusinessException(ErrorCode.ORDER_CANNOT_PAY)


enum class ErrorCode(
    val code: HttpStatus,
    val message: String
) {
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "订单不存在"),
    ORDER_PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "订单未支付"),
    ORDER_PAYMENT_CONFIRMATION_NOT_FOUND(HttpStatus.NOT_FOUND, "订单确认记录不存在"),
    ORDER_CANNOT_PAY(HttpStatus.BAD_REQUEST, "当前订单状态无法支付"),
    CLIENT_CONNECT_TIMEOUT(HttpStatus.INTERNAL_SERVER_ERROR, "client连接超时"),
    CLIENT_ERROR_RESPONSE(HttpStatus.INTERNAL_SERVER_ERROR, "client请求失败")
}
