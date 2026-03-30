package edu.cit.auditor.paluto.api

import edu.cit.auditor.paluto.dto.CookRegistrationRequest
import edu.cit.auditor.paluto.dto.CustomerRegistrationRequest
import edu.cit.auditor.paluto.dto.LoginRequest
import edu.cit.auditor.paluto.dto.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<ApiResponse<LoginResponse>>

    @POST("api/customer/register")
    suspend fun registerCustomer(
        @Body request: CustomerRegistrationRequest
    ): Response<ApiResponse<LoginResponse>>

    @POST("api/cook/register")
    suspend fun registerCook(
        @Body request: CookRegistrationRequest
    ): Response<ApiResponse<LoginResponse>>
}