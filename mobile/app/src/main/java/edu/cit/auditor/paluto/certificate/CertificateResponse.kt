package edu.cit.auditor.paluto.certificate

data class CertificateResponse(
    val id: Long,
    val cookId: Long,
    val cookName: String,
    val title: String,
    val fileUrl: String,
    val status: String,
    val adminNote: String?,
    val uploadedAt: String,
    val reviewedAt: String?,
    val deletedAt: String?
)
