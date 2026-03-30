package edu.cit.auditor.paluto.utils

import com.google.gson.Gson
import edu.cit.auditor.paluto.api.ApiResponse
import retrofit2.Response

object NetworkUtils {
    /**
     * Parses the errorBody from a Retrofit Response into a readable message.
     * Can be used by any Activity or Fragment making API calls.
     */
    fun parseError(response: Response<*>): String {
        return try {
            val errorJson = response.errorBody()?.string()
            val gson = Gson()
            // We use the standard ApiResponse DTO we created in Step 1
            val errorResponse = gson.fromJson(errorJson, ApiResponse::class.java)
            errorResponse?.error?.message ?: "An unknown error occurred"
        } catch (e: Exception) {
            "Server connection error"
        }
    }
}