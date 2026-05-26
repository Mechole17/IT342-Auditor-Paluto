package edu.cit.auditor.paluto.dto

data class ServiceRequest(
    val title: String,
    val description: String,
    val ingredientsList: String,
    val ingredientsCost: Double,
    val imageUrl: String?,
    val estPrepTime: Int,
    val servingSize: Int
)
