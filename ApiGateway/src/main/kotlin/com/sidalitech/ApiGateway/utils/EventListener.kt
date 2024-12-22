package com.sidalitech.ApiGateway.utils

import com.sidalitech.ApiGateway.model.User
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class EventListener : AbstractMongoEventListener<User>() {
    override fun onBeforeConvert(event: BeforeConvertEvent<User>) {
        super.onBeforeConvert(event)
        val user = event.source
        val now = LocalDateTime.now()

        if (user.id == null) {
            user.createdDate = now
        }
        user.lastModifiedDate = now
        super.onBeforeConvert(event)

    }
}