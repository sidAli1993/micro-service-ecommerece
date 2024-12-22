package com.sidalitech.customer_ecommerce.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/customer")
class DummyController {

    @GetMapping("/wc")
    fun getWelcome(@RequestHeader("Authorization") token:String):ResponseEntity<Any>{
        return ResponseEntity.ok("Welcome to customer micro , this is your token : $token")
    }
}