package edu.cit.auditor.paluto.dto

data class CookResponse(
    val id: Long,
    val firstname: String,
    val lastname: String,
    val hourlyRate: Double,
    val yearsXp: Int,
    val bio: String,
    val averageRating: Double
)
