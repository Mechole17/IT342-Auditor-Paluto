package edu.cit.auditor.paluto.payment

data class CheckoutRequest(
    val amount: Double,
    val serviceId: Long,
    val quantity: Int,
    val serviceAddress: String,
    val scheduledDate: String, // yyyy-MM-dd
    val scheduledTime: String  // HH:mm:ss
)