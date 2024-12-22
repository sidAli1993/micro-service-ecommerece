package com.sidalitech.customer_ecommerce

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan
class CustomerEcommereceApplication

fun main(args: Array<String>) {
	runApplication<CustomerEcommereceApplication>(*args)
}
