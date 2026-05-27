package edu.cit.auditor.paluto.services

import java.io.Serializable

data class ServiceResponse(
    val id: Long,
    val cookId: Long,
    val title: String,
    val description: String,
    val ingredientsList: String,
    val ingredientsCost: Double,
    val imageUrl: String?,
    val estPrepTime: Int,
    val servingSize: Int,
    val cookHourlyRate: Double
) : Serializable