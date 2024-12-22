package com.sidalitech.seller_ecommerce

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan
class SellerEcommerceApplication

fun main(args: Array<String>) {
	runApplication<SellerEcommerceApplication>(*args)
}
