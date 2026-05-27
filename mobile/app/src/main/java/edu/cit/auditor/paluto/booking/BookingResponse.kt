package edu.cit.auditor.paluto.dto

data class BookingResponse(
    val id: Long,
    val serviceTitle: String?,
    val serviceImage: String?,
    val quantity: Int?,
    val totalAmount: Double?,
    val scheduledDate: String?,
    val scheduledTime: String?,
    val status: String,
    val customerName: String?,
    val cookName: String?,
    val serviceAddress: String?,
    val acceptedAt: String?,
    val rejectedAt: String?,
    val completedAt: String?,
    val cancelledAt: String?,
    val createdAt: String?
)
