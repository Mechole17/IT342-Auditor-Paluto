package edu.cit.auditor.paluto.dto

data class CookRegistrationRequest(
    val firstname: String,
    val lastname: String,
    val address: String,
    val email: String,
    val password: String,
    val role: String = "COOK",
    val hourly_rate: Double,
    val years_xp: Int,
    val bio: String
)