package com.sidalitech.ApiGateway.model

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import java.util.UUID

@Document
data class User(
    @Id
    val id:String?=UUID.randomUUID().toString(),
    @Indexed(unique = true)
    @field:NotNull(message = "username should not be empty.")
    @field:Size(min = 8, max = 20, message = "username should be greater than 7 and less than 21.")
    val username:String,
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @field:NotNull(message = "password should not be empty.")
    @field:Size(min = 8, max = 14, message = "password should be greater than 7 and less than 15 characters.")
    var password:String,
    @field:NotNull(message = "email should not be empty.")
    @field:Email(message = "email format is not valid.")
    val email:String,
    @field:NotEmpty(message ="roles should not be empty." )
    val roles:List<String>,
    var createdDate: LocalDateTime?=null,
    var lastModifiedDate: LocalDateTime?=null
)
