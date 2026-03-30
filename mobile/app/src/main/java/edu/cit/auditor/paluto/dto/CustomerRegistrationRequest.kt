package edu.cit.auditor.paluto.dto

data class CustomerRegistrationRequest(
    val firstname: String,
    val lastname: String,
    val address: String,
    val email: String,
    val password: String,
    val role: String = "CUSTOMER"
)