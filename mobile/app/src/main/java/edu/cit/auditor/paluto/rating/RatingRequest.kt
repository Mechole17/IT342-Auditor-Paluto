package edu.cit.auditor.paluto.rating

data class RatingRequest(
    val bookingId: Long,
    val rating: Int,
    val comment: String?
)