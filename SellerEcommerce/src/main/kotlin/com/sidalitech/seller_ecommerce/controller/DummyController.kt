package com.sidalitech.seller_ecommerce.controller

import org.apache.coyote.Response
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/seller")
class DummyController {

    @GetMapping("/welcome")
    fun getSeller(@RequestHeader("Authorization") token:String):ResponseEntity<Any>{
        return ResponseEntity.ok("hello welcome to seller micro service this is your auth token : $token")
    }
}