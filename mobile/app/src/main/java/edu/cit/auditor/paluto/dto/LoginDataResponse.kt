package edu.cit.auditor.paluto.dto

data class LoginResponse(
    val user: UserResponse,
    val accessToken: String,
    val refreshToken: String
)

data class UserResponse(
    val email: String,
    val firstname: String,
    val lastname: String,
    val role: String
)