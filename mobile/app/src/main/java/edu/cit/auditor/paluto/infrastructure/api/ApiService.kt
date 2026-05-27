package edu.cit.auditor.paluto.infrastructure.api

import edu.cit.auditor.paluto.authentication.CookRegistrationRequest
import edu.cit.auditor.paluto.authentication.CustomerRegistrationRequest
import edu.cit.auditor.paluto.authentication.LoginRequest
import edu.cit.auditor.paluto.authentication.LoginResponse
import edu.cit.auditor.paluto.booking.BookingResponse
import edu.cit.auditor.paluto.core.UpdateProfileRequest
import edu.cit.auditor.paluto.certificate.*
import edu.cit.auditor.paluto.infrastructure.common.StorageResponse
import edu.cit.auditor.paluto.payment.CheckoutRequest
import edu.cit.auditor.paluto.rating.RatingRequest
import edu.cit.auditor.paluto.rating.RatingResponse
import edu.cit.auditor.paluto.services.ServiceRequest
import edu.cit.auditor.paluto.services.ServiceResponse
import edu.cit.auditor.paluto.users.cook.CookResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<ApiResponse<LoginResponse>>

    @GET("api/auth/me")
    suspend fun getCurrentUser(): Response<ApiResponse<LoginResponse>>

    @PUT("api/users/profile")
    suspend fun updateProfile(
        @Body request: UpdateProfileRequest
    ): Response<ApiResponse<UpdateProfileRequest>>

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

    @GET("api/services/all")
    suspend fun getAllServices(): Response<ApiResponse<List<ServiceResponse>>>

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

    @POST("api/payment/checkout")
    suspend fun checkout(
        @Body request: CheckoutRequest
    ): Response<ApiResponse<Map<String, Any>>>

    @GET("api/bookings/cook/{id}")
    suspend fun getCookBookings(
        @Path("id") id: Long
    ): Response<ApiResponse<List<BookingResponse>>>

    @PUT("api/bookings/{id}/status")
    suspend fun updateBookingStatus(
        @Path("id") id: Long,
        @Query("status") status: String,
        @Query("action") action: String
    ): Response<ApiResponse<String>>

    @GET("api/bookings/cook/{id}/stats")
    suspend fun getCookStats(
        @Path("id") id: Long
    ): Response<ApiResponse<Map<String, Any>>>

    @GET("api/bookings/cooks/{cookId}/booked-dates")
    suspend fun getBookedDates(
        @Path("cookId") cookId: Long
    ): Response<ApiResponse<List<String>>>

    // Service Management
    @GET("api/services/my-services")
    suspend fun getMyServices(): Response<ApiResponse<List<ServiceResponse>>>

    @POST("api/services/create")
    suspend fun createService(
        @Body request: ServiceRequest
    ): Response<ApiResponse<ServiceResponse>>

    @PUT("api/services/{id}")
    suspend fun updateService(
        @Path("id") id: Long,
        @Body request: ServiceRequest
    ): Response<ApiResponse<ServiceResponse>>

    @DELETE("api/services/{id}")
    suspend fun deleteService(
        @Path("id") id: Long
    ): Response<ApiResponse<Unit>>

    // Certificate Management
    @GET("api/certificates/my-certificates")
    suspend fun getMyCertificates(): Response<ApiResponse<List<CertificateResponse>>>

    @POST("api/certificates/upload")
    suspend fun uploadCertificate(
        @Body request: Map<String, String>
    ): Response<ApiResponse<CertificateResponse>>

    @DELETE("api/certificates/{id}")
    suspend fun deleteCertificate(
        @Path("id") id: Long
    ): Response<ApiResponse<Unit>>

    // Storage
    @Multipart
    @POST("api/storage/service-upload")
    suspend fun uploadServiceImage(
        @Part file: MultipartBody.Part
    ): Response<StorageResponse>

    @Multipart
    @POST("api/storage/certificate-upload")
    suspend fun uploadCertificateFile(
        @Part file: MultipartBody.Part
    ): Response<StorageResponse>
}
