package edu.cit.auditor.paluto.dto

data class RatingRequest(
    val bookingId: Long,
    val rating: Int,
    val comment: String?
)
