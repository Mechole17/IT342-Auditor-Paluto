package edu.cit.auditor.paluto.rating

data class RatingResponse(
    val id: Long,
    val bookingId: Long,
    val cookId: Long,
    val customerName: String,
    val rating: Int,
    val comment: String?,
    val createdAt: String
)