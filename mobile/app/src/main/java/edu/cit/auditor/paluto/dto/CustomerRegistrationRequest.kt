package edu.cit.auditor.paluto.dto

data class CustomerRegistrationRequest(
    val firstname: String,
    val lastname: String,
    val email: String,
    val password: String,
    val address: String,
    val role: String = "CUSTOMER",
    val auth_provider: String = "LOCAL", // Default to LOCAL
)