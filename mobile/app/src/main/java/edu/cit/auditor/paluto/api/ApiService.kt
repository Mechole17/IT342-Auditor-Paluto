package edu.cit.auditor.paluto.api

import edu.cit.auditor.paluto.dto.CookRegistrationRequest
import edu.cit.auditor.paluto.dto.CookResponse
import edu.cit.auditor.paluto.dto.CustomerRegistrationRequest
import edu.cit.auditor.paluto.dto.LoginRequest
import edu.cit.auditor.paluto.dto.LoginResponse
import edu.cit.auditor.paluto.dto.ServiceResponse
import edu.cit.auditor.paluto.dto.CertificateResponse
import edu.cit.auditor.paluto.dto.RatingResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

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

    @GET("api/cook/all")
    suspend fun getAllCooks(): Response<ApiResponse<List<CookResponse>>>

    @GET("api/cook/{id}")
    suspend fun getCookById(
        @Path("id") id: Long
    ): Response<ApiResponse<CookResponse>>

    @GET("api/services/cook/{cookId}/services")
    suspend fun getServicesByCookId(
        @Path("cookId") cookId: Long
    ): Response<ApiResponse<List<ServiceResponse>>>

    @GET("api/certificates/cook/{cookId}")
    suspend fun getCookCertificates(
        @Path("cookId") cookId: Long
    ): Response<ApiResponse<List<CertificateResponse>>>

    @GET("api/ratings/cook/{cookId}")
    suspend fun getCookRatings(
        @Path("cookId") cookId: Long
    ): Response<ApiResponse<List<RatingResponse>>>

    @GET("api/ratings/cook/{cookId}/average")
    suspend fun getCookAverageRating(
        @Path("cookId") cookId: Long
    ): Response<ApiResponse<Double>>

    @GET("api/services/{id}")
    suspend fun getServiceById(
        @Path("id") id: Long
    ): Response<ApiResponse<ServiceResponse>>
}