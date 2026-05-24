package edu.cit.auditor.paluto.api

import edu.cit.auditor.paluto.dto.*
import retrofit2.Response
import retrofit2.http.*

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

    @GET("api/bookings/customer/{userId}")
    suspend fun getCustomerBookings(
        @Path("userId") userId: Long
    ): Response<ApiResponse<List<BookingResponse>>>

    @GET("api/bookings/{id}")
    suspend fun getBookingById(
        @Path("id") id: Long
    ): Response<ApiResponse<BookingResponse>>

    @PUT("api/bookings/{id}/cancel-booking")
    suspend fun cancelBooking(
        @Path("id") id: Long
    ): Response<ApiResponse<Any?>>

    @GET("api/ratings/check/{bookingId}")
    suspend fun checkIfRated(
        @Path("bookingId") bookingId: Long
    ): Response<ApiResponse<Boolean>>

    @POST("api/ratings/submit")
    suspend fun submitRating(
        @Body request: RatingRequest
    ): Response<ApiResponse<Any?>>
}